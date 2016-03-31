package com.adrian.goodstart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.adrian.goodstart.activity.ActivityMain;
import com.adrian.goodstart.service.MyService;
import com.adrian.goodstart.tool.CmdUtil2;
import com.adrian.goodstart.tool.CommUtils;
import com.adrian.goodstart.tool.Constants;
import com.adrian.goodstart.tool.DataUtil;
import com.adrian.goodstart.tool.NetTool;


public class MyReceiver extends BroadcastReceiver {

    private DataUtil dataUtil;
    private CmdUtil2 cmdUtil;

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) && System.currentTimeMillis() - Constants.curMillis > 5000) {
            Constants.curMillis = System.currentTimeMillis();
            if (CommUtils.getWifiStatus(context)) {
                CommUtils.showToast(context, "wifi已连接");
                String ssid = CommUtils.getWifiName(context);
                if (!ssid.contains("IYK")) {
                    startScan(context);
                }
            } else {
                CommUtils.showToast(context, "WIFI未连接");
            }
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, MyService.class);
            context.startService(i);
        }
    }

    private void startScan(Context context) {
//

        if (dataUtil == null) {
            dataUtil = new DataUtil(context);
        }

        String ip = dataUtil.getIp();
        if (TextUtils.isEmpty(ip)) {
            CommUtils.showToast(context, "请先连接设备");
        } else {
            byte[] cmdBytes = dataUtil.getCmdBytes(0);
            if (cmdBytes != null && cmdBytes.length > 0) {
                ActivityMain.mConnetListening.SendCmdCode(0x32, cmdBytes, cmdBytes.length, ip);
            } else {
                CommUtils.showToast(context, "请先学习指令");
            }
        }

        NetTool tool = new NetTool(context);
        tool.scan();
    }
}
