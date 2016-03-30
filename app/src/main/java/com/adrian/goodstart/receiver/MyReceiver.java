package com.adrian.goodstart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.adrian.goodstart.service.MyService;
import com.adrian.goodstart.tool.CmdUtil;
import com.adrian.goodstart.tool.CommUtils;
import com.adrian.goodstart.tool.Constants;
import com.adrian.goodstart.tool.NetTool;


public class MyReceiver extends BroadcastReceiver {

    private CmdUtil util;

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) && System.currentTimeMillis() - Constants.curMillis > 5000) {
            Constants.curMillis = System.currentTimeMillis();
            if (CommUtils.getWifiStatus(context)) {
                CommUtils.showToast(context, "wifi已连接");
                startScan(context);
            } else {
                CommUtils.showToast(context, "WIFI未连接");
            }
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, MyService.class);
            context.startService(i);
        }
    }

    private void startScan(Context context) {
//        if (!TextUtils.isEmpty(CmdUtil.getInstance(context).getSsid())) {
//            CmdUtil.getInstance(context).connDevice(CmdUtil.getInstance(context).getSsid());
//        }

//        CmdUtil.getInstance(context).setBshortredid(R.id.button_shortredA);
//        if(CmdUtil.getInstance(context).isBshortredflagA()){
//            CmdUtil.getInstance(context).sendCmdCode(0x32, CmdUtil.shortredcodeA, CmdUtil.shortredcodeA.length, CmdUtil.getInstance(context).getIp());
//        } else {
//            CommUtils.showToast(context, "未学习开机功能，请打开应用先学习该功能");
//        }

        if (util == null) {
            util = new CmdUtil(context);
        }

        if (util.isConnectedDev()) {
            byte[] cmd = util.getCmdBytes(0);
            String ip = util.getIp();
            if (cmd != null && cmd.length > 0) {
                util.sendCmdCode(0x32, cmd, cmd.length, ip);
            } else {
                CommUtils.showToast(context, "请先学习指令");
            }
        } else {
            CommUtils.showToast(context, "请先连接设备");
        }

        NetTool tool = new NetTool(context);
        tool.scan();
    }
}
