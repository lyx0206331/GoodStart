package com.adrian.goodstart.tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.adrian.goodstart.R;
import com.adrian.goodstart.dao.CmdDao;
import com.adrian.goodstart.dao.DaoMaster;
import com.adrian.goodstart.dao.DaoSession;
import com.adrian.goodstart.pojo.Cmd;
import com.adrian.goodstart.view.LoadingDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adrian on 16-3-27.
 */
public class CmdUtil {
    private static CmdUtil ourInstance;

    private Context context;

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private CmdDao cmdDao;

    public ConnetListening mConnetListening = null;
    private CmdCheck mCmdCheck;
    public static byte[] shortredcodeA = new byte[512], shortredcodeB = new byte[512];
    public static byte[] longredcodeA = new byte[1024], longredcodeB = new byte[1024];
    public static byte[] m315codeA = new byte[12], m315codeB = new byte[12],
            m315codeC = new byte[12], m315codeD = new byte[12];
    private byte[] zuheredcodeA = new byte[512], zuheredcodeB = new byte[512], zuheredcodeC = new byte[512], zuheredcodeD = new byte[512];

    private String ip;
    private List<ScanResult> mWifiList;
    private static Handler mmHandler = null;
    private boolean bshortredflagA = false, bshortredflagB = false, blongredflagA = false, blongredflagB = false;
    private boolean b315flagA = false, b315flagB = false, b315flagC = false, b315flagD = false;
    private boolean bzuheflag = false;
    private int b315id, bshortredid, blongredid;
    private final CharSequence[] items = {"学习315M射频码", "创建315M射频编码", "取消"};
    private String helpstudy = "请将遥控器对准设备，并按下要学习的按键！";
    private boolean StudyCodeCheckwork = false;
    private boolean studybuttonsflag = false;
    private int studybuttonCount = 0;
    private SendCodeMoreButtonThred mSendCodeMoreButtonThred;
    private StudyCodeCheckThred mStudyCodeCheckThred;
    private StudyCodeMoreButtonThred mStudyCodeMoreButtonThred;
    private ProgressDialog mProgressDialog = null;

//    private String ssid;

//    public static CmdUtil getInstance(Context context) {
//        if (ourInstance == null) {
//            ourInstance = new CmdUtil(context);
//        }
//        return ourInstance;
//    }

    public CmdUtil(Context ctx) {
        context = ctx;
        initDao();
        initConnetListening();
    }

    private void initDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "cmds_db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        cmdDao = daoSession.getCmdDao();
    }

//    public void updateCmd(byte[] cmdBytes, String localIp) {
//        cmdDao.deleteAll();
//        cmdDao.insert(new Cmd(null, cmdBytes, localIp, 0, null));
//    }

    public List<Cmd> getCmds() {
        List<Cmd> cmds = cmdDao.queryBuilder().list();
        if (cmds != null) {
            return cmds;
        }
        return null;
    }

//    public void updateCmd(byte[] cmdBytes, String ip, Integer type) {
//        List<Cmd> cmds = cmdDao.queryBuilder().where(CmdDao.Properties.ip.eq(ip), CmdDao.Properties.type.eq(type)).list();
//        if (cmds != null && cmds.size() > 0) {
//            cmdDao.deleteInTx(cmds);
//        }
//        cmdDao.insert(new Cmd(null, cmdBytes, ip, type, null));
//    }

    public Cmd getCmd(Integer type) {
        List<Cmd> cmds = cmdDao.queryBuilder().where(CmdDao.Properties.type.eq(type)).list();
        if (cmds != null && cmds.size() > 0) {
            return cmds.get(0);
        }
        return null;
    }

    public void updateCmd(byte[] cmdBytes, String ip, Integer type, String ssid) {
//        this.ssid = ssid;
        LogUtil.e("TAG", "update : " + ip + " || " + type + " || " + ssid);
        List<Cmd> cmds = cmdDao.queryBuilder().where(CmdDao.Properties.type.eq(type)).list();
        if (cmds != null && cmds.size() > 0) {
//            cmdDao.deleteInTx(cmds);
            Cmd cmd = cmds.get(0);
//            LogUtil.e("TAG", "update data : " + cmds.size() + "///" + cmd.toString());
            cmd.setIp(ip);
            cmd.setCmd(cmdBytes);
            cmd.setSsid(ssid);
            cmdDao.update(cmd);
        } else {
            LogUtil.e("TAG", "insert data");
            cmdDao.insert(new Cmd(null, cmdBytes, ip, type, ssid));
        }

        LogUtil.e("TAG", "update data : " + getCmd(type).toString());
    }

//    public Cmd getCmd(Integer type, String ssid) {
//        List<Cmd> cmds = cmdDao.queryBuilder().where(CmdDao.Properties.ssid.eq(ssid), CmdDao.Properties.type.eq(type)).list();
//        if (cmds != null && cmds.size() > 0) {
//            return cmds.get(0);
//        }
//        return null;
//    }

    public void initConnetListening() {
        mConnetListening = new ConnetListening(context, mHandler);
        mConnetListening.StartListen();
        mConnetListening.ConnectWifiScan();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConnetListening.LISTENING_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ConnetListening.LISTENING_CONNECTED:

                            break;
                        case ConnetListening.LISTENING_CONNECTFAILD:

                            break;
                        case ConnetListening.LISTENING_NONE:

                            break;
                    }
                    break;
                case ConnetListening.LISTENING_MESSAGE_STATE:
                    switch (msg.arg1) {
                        case ConnetListening.MESSAGE_READSUCCESS:

                            mCmdCheck = new CmdCheck((byte[]) msg.obj, msg.arg2);
                            CmdCheckState(mCmdCheck.getCmd());
                            Log.e("ActivityMain", "MESSAGE_READSUCCESS" + (mCmdCheck.getCmd() & 0xff));
                            if (mmHandler != null) {
                                mmHandler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj).sendToTarget();
                            }
                            break;
                        case ConnetListening.MESSAGE_READFAILD:
                            Log.e("ActivityMain", "ConnetListening_MESSAGE_READFAILD");
                            break;
                    }
                    break;
            }
        }
    };

    public void sendCmdCode(int code, byte[] buff, int len, String ip) {
        final byte[] buf = null;
        if (ip == null || ip.equals("")) return;
        mConnetListening.SendCmdCode(code, buff, len, ip);
    }

    private void CmdCheckState(final int cmd) {
        int code;
        Log.e("CmdCheckState", "code-->" + cmd);
        switch (cmd) {
            case (byte) 0x9C:
                Deviceinit(mCmdCheck.getCmdContentNoStateBuf());
                break;
            case (byte) 0x86://红外学习应答指令
            case (byte) 0xB0://短红外学习应答指令
                if (cmd == (byte) 0x86) {
                    code = 0x08;
                } else {
                    code = 0x31;
                }
                if (mStudyCodeCheckThred == null) {
                    mStudyCodeCheckThred = new StudyCodeCheckThred(code, 0x07);//开始查询学习状态；
                    StudyCodeCheckwork = true;
                    mStudyCodeCheckThred.start();
                } else {
                    mStudyCodeCheckThred.cancel();
                    mStudyCodeCheckThred = null;
                    mStudyCodeCheckThred = new StudyCodeCheckThred(code, 0x07);//开始查询学习状态；
                    StudyCodeCheckwork = true;
                    mStudyCodeCheckThred.start();
                }
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(context, "提示！", "", true, true);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    //mProgressDialog.setCancelable(false);
                }
                mProgressDialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        if (mStudyCodeCheckThred != null) {
                            StudyCodeCheckwork = false;
                            mStudyCodeCheckThred.cancel();
                            mStudyCodeCheckThred = null;
                            mProgressDialog.dismiss();
                        }
                        studybuttonsflag = false;
                    }
                });
                mProgressDialog.setMessage("学习红外编码!" + "\n" + helpstudy);
                mProgressDialog.show();
                break;
            case (byte) 0x8A://315M学习应答指令
                Log.e("mCmdCheck-->", "case：0x8A");
                if (mStudyCodeCheckThred == null) {
                    mStudyCodeCheckThred = new StudyCodeCheckThred(0x0C, 0x0B);//开始查询学习状态；
                    StudyCodeCheckwork = true;
                    mStudyCodeCheckThred.start();
                } else {
                    mStudyCodeCheckThred.cancel();
                    mStudyCodeCheckThred = null;
                    mStudyCodeCheckThred = new StudyCodeCheckThred(0X0C, 0x0B);//开始查询学习状态；
                    StudyCodeCheckwork = true;
                    mStudyCodeCheckThred.start();
                }
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(context, "提示！", "", true, true);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                }
                mProgressDialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        if (mStudyCodeCheckThred != null) {
                            StudyCodeCheckwork = false;
                            mStudyCodeCheckThred.cancel();
                            mStudyCodeCheckThred = null;
                            mProgressDialog.dismiss();
                        }
                    }
                });
                mProgressDialog.setMessage("学习315M射频编码!" + "\n" + helpstudy);
                mProgressDialog.show();
                break;
            case (byte) 0x88://红外查询应答指令
            case (byte) 0xB1://短红外查询应答指令
                if (mCmdCheck.getCmdSetState() != 1) break;
                StudyCodeCheckwork = false;
                mStudyCodeCheckThred.cancel();
                mStudyCodeCheckThred = null;
                byte[] buf = mCmdCheck.getCmdContentNoStateBuf();
                if (studybuttonsflag) {
                    switch (studybuttonCount) {
                        case 0:
                            zuheredcodeA = buf;
                            break;
                        case 1:
                            zuheredcodeB = buf;
                            break;
                        case 2:
                            zuheredcodeC = buf;
                            break;
                        case 3:
                            zuheredcodeD = buf;
                            break;
                    }
                    bzuheflag = true;
                    studybuttonCount++;
                    if (studybuttonCount >= 4) {
                        mConnetListening.SendCmdCode(0x07, buf, 0, ip);
                        mProgressDialog.dismiss();
                        studybuttonsflag = false;
                        studybuttonCount = 0;
                    } else {
                        mProgressDialog.setMessage("已学习" + String.valueOf(studybuttonCount) + "个红外编码！"
                                + "\n" + "请按提示再按下一个要学习的红外按键！");
                        mStudyCodeMoreButtonThred = new StudyCodeMoreButtonThred();
                        mStudyCodeMoreButtonThred.start();
                    }
                } else {
                    //MainActivity.mConnetListening.SendCmdCode(0x07,buf,0,deviceip);
                    if (cmd == (byte) 0x88) {
//                        switch(blongredid){
//                            case R.id.button_longredA:
//                                blongredflagA = true;
//                                longredcodeA = buf;
//                                break;
//                            case R.id.button_longredB:
//                                blongredflagB = true;
//                                longredcodeB = buf;
//                                break;
//                        }
                    } else {
                        switch(bshortredid){
                            case R.id.btn_on:
                                bshortredflagA = true;
                                shortredcodeA = buf;

                                updateCmd(shortredcodeA, getIp(), 0, getSsid());
//                                CmdUtil.getInstance(ActivityMain.this).saveCmd(shortredcodeA, CommUtils.getIp4Wifi(ActivityMain.this), 0); //保存指令

//                                mConnetListening.SendCmdCode(0x32,shortredcodeA,shortredcodeA.length,ip);   //避免重复执行
                                break;
                            case R.id.btn_off:
                                bshortredflagB = true;
                                shortredcodeB = buf;

                                updateCmd(shortredcodeB, getIp(), 1, getSsid());
//                                CmdUtil.getInstance(ActivityMain.this).saveCmd(shortredcodeB, CommUtils.getIp4Wifi(ActivityMain.this), 1); //保存指令

//                                mConnetListening.SendCmdCode(0x32,shortredcodeB,shortredcodeB.length,ip);   //避免重复执行
                                break;
                        }
                    }
                    mProgressDialog.dismiss();
                }
                break;
            case (byte) 0x87:
                if (!studybuttonsflag) {
                    if (mStudyCodeCheckThred != null) {
                        mStudyCodeCheckThred.cancel();
                        StudyCodeCheckwork = false;
                        mStudyCodeCheckThred = null;
                    }
                }
                break;
            case (byte) 0x8B:
                if (mStudyCodeCheckThred != null) {
                    mStudyCodeCheckThred.cancel();
                    StudyCodeCheckwork = false;
                    mStudyCodeCheckThred = null;
                }
                break;
            case (byte) 0x8C:
                if (mCmdCheck.getCmdSetState() != 1) break;
                if (mStudyCodeCheckThred != null) {
                    mStudyCodeCheckThred.cancel();
                    StudyCodeCheckwork = false;
                    mStudyCodeCheckThred = null;
                }
                mProgressDialog.dismiss();
                byte[] buff = mCmdCheck.getCmdContentNoStateBuf();
//                switch(b315id){
//                    case R.id.button_315a:
//                        m315codeA = buff;
//                        b315flagA = true;
//                        break;
//                    case R.id.button_315b:
//                        m315codeB = buff;
//                        b315flagB = true;
//                        break;
//                    case R.id.button_315c:
//                        m315codeC = buff;
//                        b315flagC = true;
//                        break;
//                    case R.id.button_315d:
//                        m315codeD = buff;
//                        b315flagD = true;
//                        break;
//                }
                break;
        }

    }

    public void Deviceinit(byte[] buf) {
        String ss = new String(buf, 0, buf.length);
        String[] args = ss.split(",");
        ip = args[0];
        Log.e("GetIp-->", "ip:" + ip);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        Toast toast = Toast.makeText(context, "连接设备成功，获取IP：" + ip, Toast.LENGTH_SHORT);
        toast.show();

//        bshortredid = R.id.button_shortredA;
//        Cmd cmd = getCmd(0);
//        if (cmd != null && cmd.getCmd() != null && cmd.getCmd().length > 0) {
//            sendCmdCode(0x32, cmd.getCmd(), cmd.getCmd().length, ip);
//        }
        String ssid = getSsid();
        LogUtil.e("TAG", "ip --- ssid : " + ip + "---" + ssid);
        updateCmd(null, ip, 0, ssid);
        updateCmd(null, ip, 1, ssid);
//        CommUtils.showToast(context, "open cmd : " + Arrays.toString(shortredcodeA));
//        if (shortredcodeA != null) {
//            sendCmdCode(0x32, shortredcodeA, shortredcodeA.length, ip);
//        }
    }

    public void DeviceList() {
        mWifiList = mConnetListening.ConnectWifiScanResult();
        if (mWifiList == null) return;
        Map<String, Object> list = new HashMap<String, Object>();
        for (int i = 0, j = 0; i < mWifiList.size(); i++) {
            if (mWifiList.get(i).SSID.toString().contains("IYK")) {
                list.put(String.valueOf(j++), mWifiList.get(i).SSID.toString());
            }
        }
        if (list.size() == 0) return;
        final CharSequence[] item = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            item[i] = list.get(String.valueOf(i)).toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示！");
        builder.setItems(item, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String ssid = item[which].toString();
//                mConnetListening.getLinkIPBroadcast(item[which].toString());
                connDevice(ssid);
                Log.e("CmdUtil", "getLinkIPBroadcast:" + item[which].toString());
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(context, "提示！", "正在获取" + item[which].toString() + "的ip...", true, true);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                } else {
                    mProgressDialog.setMessage("正在获取" + item[which].toString() + "的ip...");
                    mProgressDialog.show();
                }
            }
        }).create().show();
//        AlertDialog mAlerDialog = builder.create();
//        mAlerDialog.show();
    }

    public void showProgressDialog(Activity act, String title, String message) {
//        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(act, title, message, true, true);
            mProgressDialog.setCanceledOnTouchOutside(false);
//        } else {
//            mProgressDialog.setMessage(message);
//            mProgressDialog.show();
//        }
    }

    public void connDevice(String ssid) {
//        LogUtil.e("TAG", "linkIPBroadcast ssid: " + ssid);
        mConnetListening.getLinkIPBroadcast(ssid);
    }

    public static void setHandler(Handler handler) {
        mmHandler = handler;
    }

    public boolean isBshortredflagA() {
        return bshortredflagA;
    }

    public void setBshortredflagA(boolean bshortredflagA) {
        this.bshortredflagA = bshortredflagA;
    }

    public boolean isBshortredflagB() {
        return bshortredflagB;
    }

    public void setBshortredflagB(boolean bshortredflagB) {
        this.bshortredflagB = bshortredflagB;
    }

    public int getBshortredid() {
        return bshortredid;
    }

    public void setBshortredid(int bshortredid) {
        this.bshortredid = bshortredid;
    }

    public boolean isStudybuttonsflag() {
        return studybuttonsflag;
    }

    public void setStudybuttonsflag(boolean studybuttonsflag) {
        this.studybuttonsflag = studybuttonsflag;
    }

    public int getStudybuttonCount() {
        return studybuttonCount;
    }

    public void setStudybuttonCount(int studybuttonCount) {
        this.studybuttonCount = studybuttonCount;
    }

    public boolean isStudyCodeCheckwork() {
        return StudyCodeCheckwork;
    }

    public void setStudyCodeCheckwork(boolean studyCodeCheckwork) {
        StudyCodeCheckwork = studyCodeCheckwork;
    }

    public String getHelpstudy() {
        return helpstudy;
    }

    public void setHelpstudy(String helpstudy) {
        this.helpstudy = helpstudy;
    }

    public CharSequence[] getItems() {
        return items;
    }

    public String getIp() {
        List<Cmd> cmds = getCmds();
        for (Cmd c :
                cmds) {
            LogUtil.e("TAG", "c : " + c.toString());
            if (!TextUtils.isEmpty(c.getIp())){
                String ip = c.getIp();
                LogUtil.e("TAG", "get ip : " + ip);
                return ip;
            }
        }
        return null;
    }

//    public String getIp(Integer type) {
//        Cmd cmd = getCmd(type);
//        LogUtil.e("TAG", "get ip : " + (cmd == null ? "cmd is null" : cmd.toString()));
//        if (cmd == null) {
//            return null;
//        }
//        return cmd.getIp();
//    }

    public byte[] getCmdBytes(Integer type) {
        Cmd cmd = getCmd(type);
        if (cmd == null) {
            return  null;
        }
        return cmd.getCmd();
    }

//    public void setIp(String ip) {
//        this.ip = ip;
//    }

    public boolean isConnectedDev() {
        LogUtil.e("TAG", "ip : " + getIp());
        if (TextUtils.isEmpty(getIp())) {
//            CommUtils.showToast(context, "请先连接设备");
            return false;
        }
        return true;
    }

    public String getSsid() {
//        return ssid;
        Cmd cmd = getCmd(0);
        if (cmd == null) {
            return null;
        }
        return cmd.getSsid();
    }

    public void setSsid(String ssid) {
//        this.ssid = ssid;
        updateCmd(null, ip, 0, ssid);
        updateCmd(null, ip, 1, ssid);
    }

    private void ButtonAlertDialog315() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示！");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        final byte[] buf = null;
                        if (ip == null || ip.equals("")) return;
                        mConnetListening.SendCmdCode(0x0A, buf, 0, ip);
                        break;
                    case 1:
                        CreateSendCode315M();
                        break;
                }
            }
        });
        AlertDialog mAlerDialog = builder.create();
        mAlerDialog.show();


    }

    private void CreateSendCode315M() {

        final Calendar c = Calendar.getInstance();
        byte[] sendcode = new byte[12];
        int second = c.get(Calendar.HOUR) * 3600 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);
        // 地址码高位0
        switch ((int) (second / 34992)) {
            case 0:
                sendcode[0] = 0;
                break;
            case 1:
                sendcode[0] = 1;
                break;
            case 2:
                sendcode[0] = 0x0f;
                break;
        }
        // 地址码高位1
        switch ((int) ((second % 34992) / 11664)) {
            case 0:
                sendcode[1] = 0;
                break;
            case 1:
                sendcode[1] = 1;
                break;
            case 2:
                sendcode[1] = 0x0f;
                break;
        }

        // 地址码高位2
        switch ((int) ((second % 11664) / 3888)) {
            case 0:
                sendcode[2] = 0;
                break;
            case 1:
                sendcode[2] = 1;
                break;
            case 2:
                sendcode[2] = 0x0f;
                break;
        }
        // 地址码高位3
        switch ((int) ((second % 3888) / 1296)) {
            case 0:
                sendcode[3] = 0;
                break;
            case 1:
                sendcode[3] = 1;
                break;
            case 2:
                sendcode[3] = 0x0f;
                break;
        }

        // 地址码高位4
        switch ((int) ((second % 1296) / 432)) {
            case 0:
                sendcode[4] = 0;
                break;
            case 1:
                sendcode[4] = 1;
                break;
            case 2:
                sendcode[4] = 0x0f;
                break;
        }
        // 地址码高位5
        switch ((int) ((second % 432) / 144)) {
            case 0:
                sendcode[5] = 0;
                break;
            case 1:
                sendcode[5] = 1;
                break;
            case 2:
                sendcode[5] = 0x0f;
                break;
        }

        // 地址码高位6
        switch ((int) ((second % 144) / 48)) {
            case 0:
                sendcode[6] = 0;
                break;
            case 1:
                sendcode[6] = 1;
                break;
            case 2:
                sendcode[6] = 0x0f;
                break;
        }
        // 地址码高位7
        switch ((int) ((second % 48) / 16)) {
            case 0:
                sendcode[7] = 0;
                break;
            case 1:
                sendcode[7] = 1;
                break;
            case 2:
                sendcode[7] = 0x0f;
                break;
        }
        // 地址码高位8
        switch ((int) ((second % 16) / 8)) {
            case 0:
                sendcode[8] = 0;
                break;
            case 1:
                sendcode[8] = 1;
                break;
        }
        // 地址码高位9
        switch ((int) ((second % 8) / 4)) {
            case 0:
                sendcode[9] = 0;
                break;
            case 1:
                sendcode[9] = 1;
                break;
        }
        // 地址码高位10
        switch ((int) ((second % 4) / 2)) {
            case 0:
                sendcode[10] = 0;
                break;
            case 1:
                sendcode[10] = 1;
                break;
        }
        // 地址码高位11
        switch ((int) (second % 2)) {
            case 0:
                sendcode[11] = 0;
                break;
            case 1:
                sendcode[11] = 1;
                break;
        }
//        switch(b315id){
//            case R.id.button_315a:
//                m315codeA = sendcode;
//                b315flagA = true;
//                break;
//            case R.id.button_315b:
//                m315codeB = sendcode;
//                b315flagB = true;
//                break;
//            case R.id.button_315c:
//                m315codeC = sendcode;
//                b315flagC = true;
//                break;
//            case R.id.button_315d:
//                m315codeD = sendcode;
//                b315flagD = true;
//                break;
//
//        }
    }

    private class StudyCodeMoreButtonThred extends Thread {
        byte[] buf;

        public StudyCodeMoreButtonThred() {
        }

        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (studybuttonsflag) {
                mConnetListening.SendCmdCode(0x30, buf, 0, ip); // 0X30为短红外指令 0x06是长红外指令
            }
        }
    }

    private class SendCodeMoreButtonThred extends Thread {

        public void run() {
            for (int i = 0; i < studybuttonCount; i++) {
                //0X32短红外学习码 0x09为红外学习码
                switch (i) {
                    case 0:
                        mConnetListening.SendCmdCode(0x32, zuheredcodeA, zuheredcodeA.length, ip);
                        break;
                    case 1:
                        mConnetListening.SendCmdCode(0x32, zuheredcodeB, zuheredcodeB.length, ip);
                        break;
                    case 2:
                        mConnetListening.SendCmdCode(0x32, zuheredcodeC, zuheredcodeC.length, ip);
                        break;
                    case 3:
                        mConnetListening.SendCmdCode(0x32, zuheredcodeD, zuheredcodeD.length, ip);
                        break;
                }
                try {
                    Thread.sleep(1500);//1.5s
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    private class StudyCodeCheckThred extends Thread {
        private int checkcmd;
        private int stopcmd;
        byte[] buf;

        public StudyCodeCheckThred(int checkcmd, int stopcmd) {
            this.checkcmd = checkcmd;
            this.stopcmd = stopcmd;
        }

        public void run() {
            while (StudyCodeCheckwork) {
                try {
                    Thread.sleep(1500);//1.5s
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (StudyCodeCheckwork) {
                    mConnetListening.SendCmdCode(checkcmd, buf, 0, ip);//查询学习状态
                } else {
                    mConnetListening.SendCmdCode(stopcmd, buf, 0, ip);
                    break;
                }
            }
        }

        public void cancel() {
            StudyCodeCheckwork = false;
        }
    }
}
