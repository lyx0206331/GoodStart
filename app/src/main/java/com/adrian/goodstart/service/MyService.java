package com.adrian.goodstart.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.adrian.goodstart.tool.CommUtils;
import com.adrian.goodstart.tool.TransFileUtil;
import com.adrian.goodstart.view.LoadingDialog;


public class MyService extends Service implements TransFileUtil.Callback {

    private TransFileUtil tcpUtil;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CommUtils.showToast(this, "create service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        showLoadingDialog("asdfasdfasdfsadfsdf");
        CommUtils.showToast(this, "start command");
        if (tcpUtil == null) {
            tcpUtil = new TransFileUtil();
            tcpUtil.setCallback(this);
        }
        if (CommUtils.getWifiStatus(this)) {

//            ProgressDialog.show(this, "title", "message");

//            CmdUtil.getInstance(this).setBshortredid(R.id.button_shortredA);
//            if(CmdUtil.getInstance(this).isBshortredflagA()){
//                CmdUtil.getInstance(this).sendCmdCode(0x32, CmdUtil.shortredcodeA, CmdUtil.shortredcodeA.length, CmdUtil.getInstance(this).getIp());
//            } else {
//                CommUtils.showToast(this, "未学习开机功能，请打开应用先学习该功能");
//            }

//            Cmd cmd = CmdUtil.getInstance(this).getCmd(0);
//            if (cmd != null && !TextUtils.isEmpty(cmd.getSsid())) {
//                CmdUtil.getInstance(this).connDevice(cmd.getSsid());
//            }
            try {
                String ip = intent.getExtras().getString("ip");
                tcpUtil.setIp(ip);
                tcpUtil.seriesUpload(this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            CommUtils.showToast(this, "请先连接WIFI");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void exception(int errorCode) {

    }

    @Override
    public void updateProgress(int progress) {
        handler.sendEmptyMessage(progress);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    CommUtils.showToast(MyService.this, "上传成功");
                    break;
            }
        }
    };
}
