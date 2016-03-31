package com.adrian.goodstart.tool;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.adrian.goodstart.R;

/**
 * Created by adrian on 16-3-31.
 */
public class CmdUtil2 {
    private Context context;

    public static ConnetListening mConnetListening = null;
    private CmdCheck mCmdCheck;
    public static byte[] shortredcodeA = new byte[512], shortredcodeB = new byte[512];
    private byte[] zuheredcodeA = new byte[512], zuheredcodeB = new byte[512], zuheredcodeC = new byte[512], zuheredcodeD = new byte[512];

    private String ip;
    private String helpstudy = "请将遥控器对准设备，并按下要学习的按键！";
    private boolean bshortredflagA = false, bshortredflagB = false, blongredflagA = false, blongredflagB = false;
    private boolean StudyCodeCheckwork = false;
    private boolean studybuttonsflag = false;
    private int studybuttonCount = 0;
    private int b315id, bshortredid, blongredid;
    private SendCodeMoreButtonThred mSendCodeMoreButtonThred;
    private StudyCodeCheckThred mStudyCodeCheckThred;
    private StudyCodeMoreButtonThred mStudyCodeMoreButtonThred;
    private ProgressDialog mProgressDialog = null;

    public CmdUtil2(Context context) {
        this.context = context;
        initConnetListening();
    }

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
//                            if (mmHandler != null) {
//                                mmHandler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj).sendToTarget();
//                            }
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
//                if (mProgressDialog == null) {
//                    mProgressDialog = ProgressDialog.show(context, "提示！", "", true, true);
//                    mProgressDialog.setCanceledOnTouchOutside(false);
//                }
//                mProgressDialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
//
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        // TODO Auto-generated method stub
//                        if (mStudyCodeCheckThred != null) {
//                            StudyCodeCheckwork = false;
//                            mStudyCodeCheckThred.cancel();
//                            mStudyCodeCheckThred = null;
//                            mProgressDialog.dismiss();
//                        }
//                    }
//                });
//                mProgressDialog.setMessage("学习315M射频编码!" + "\n" + helpstudy);
//                mProgressDialog.show();
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
//                    bzuheflag = true;
//                    studybuttonCount++;
//                    if (studybuttonCount >= 4) {
//                        mConnetListening.SendCmdCode(0x07, buf, 0, ip);
//                        mProgressDialog.dismiss();
//                        studybuttonsflag = false;
//                        studybuttonCount = 0;
//                    } else {
//                        mProgressDialog.setMessage("已学习" + String.valueOf(studybuttonCount) + "个红外编码！"
//                                + "\n" + "请按提示再按下一个要学习的红外按键！");
//                        mStudyCodeMoreButtonThred = new StudyCodeMoreButtonThred();
//                        mStudyCodeMoreButtonThred.start();
//                    }
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
                                break;
                            case R.id.btn_off:
                                bshortredflagB = true;
                                shortredcodeB = buf;
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
//                mProgressDialog.dismiss();
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
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//            mProgressDialog = null;
//        }
        Toast toast = Toast.makeText(context, "连接设备成功，获取IP：" + ip, Toast.LENGTH_SHORT);
        toast.show();
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
