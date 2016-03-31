package com.adrian.goodstart.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.adrian.goodstart.dao.CmdDao;
import com.adrian.goodstart.dao.DaoMaster;
import com.adrian.goodstart.dao.DaoSession;
import com.adrian.goodstart.pojo.Cmd;

import java.util.List;

/**
 * Created by adrian on 16-3-31.
 */
public class DataUtil {

    private Context context;

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private CmdDao cmdDao;

    public DataUtil(Context context) {
        this.context = context;

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "cmds_db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        cmdDao = daoSession.getCmdDao();
    }

    public List<Cmd> getCmds() {
        List<Cmd> cmds = cmdDao.queryBuilder().list();
        if (cmds != null) {
            return cmds;
        }
        return null;
    }

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

    public byte[] getCmdBytes(Integer type) {
        Cmd cmd = getCmd(type);
        if (cmd == null) {
            return  null;
        }
        return cmd.getCmd();
    }

    public String getSsid() {
//        return ssid;
        Cmd cmd = getCmd(0);
        if (cmd == null) {
            return null;
        }
        return cmd.getSsid();
    }

    public void updateSsid(String ssid, Integer type) {
        Cmd cmd = getCmd(type);
        if (cmd == null) {
            cmdDao.insert(new Cmd(null, null, null, type, ssid));
        } else {
            cmd.setSsid(ssid);
            cmdDao.update(cmd);
        }
    }

    public void updateIp(String ip, Integer type) {
        Cmd cmd = getCmd(type);
        if (cmd == null) {
            cmdDao.insert(new Cmd(null, null, ip, type, null));
        } else {
            cmd.setIp(ip);
            cmdDao.update(cmd);
        }
    }

    public void updateCmdBytes(byte[] cmdBytes, Integer type) {
        Cmd cmd = getCmd(type);
        if (cmd == null) {
            return;
        }
        cmd.setCmd(cmdBytes);
        cmdDao.update(cmd);
    }

    public void clear() {
        cmdDao.deleteAll();
    }

    public void printCmds() {
        Log.e("DATA", "print data");
        List<Cmd> cmds = getCmds();
        for (Cmd cmd :
                cmds) {
            Log.e("DATA", cmd.toString());
        }
    }
}
