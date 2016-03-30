package com.adrian.goodstart.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.adrian.goodstart.R;
import com.adrian.goodstart.pojo.Cmd;
import com.adrian.goodstart.tool.CmdUtil;
import com.adrian.goodstart.tool.CommUtils;
import com.adrian.goodstart.tool.LogUtil;
import com.adrian.goodstart.tool.NetTool;
import com.adrian.goodstart.view.LoadingDialog;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button mSettingsBtn;
    private Button mConnBtn;
    private Button mOnBtn;
    private Button mOffBtn;
    private Button mShareBtn;
    
    public static CmdUtil util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        util = new CmdUtil(this);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_main);
        mSettingsBtn = (Button) findViewById(R.id.btn_settings);
        mConnBtn = (Button) findViewById(R.id.btn_conn);
        mOnBtn = (Button) findViewById(R.id.btn_on);
        mOffBtn = (Button) findViewById(R.id.btn_off);
        mShareBtn = (Button) findViewById(R.id.btn_share);

        mSettingsBtn.setOnClickListener(this);
        mConnBtn.setOnClickListener(this);
        mOffBtn.setOnClickListener(this);
        mOnBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        util.mConnetListening.ConnectWifiScanResult();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_settings:
//                String[] items = new String[]{
//                        "abcdef","asdfadsfa"
//                };
//                new AlertDialog.Builder(this).setTitle("title").setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).create().show();
                startActivity(SettingsActivity.class);
                break;
            case R.id.btn_conn:
                String ssid = util.getSsid();
                LogUtil.e("TAG", "conn ssid : " + ssid);
                if (TextUtils.isEmpty(ssid)) {
                    CommUtils.showToast(this, "请先配置遥控器网络");
                } else {
//                    util.mConnetListening.getLinkIPBroadcast(ssid);
                    util.showProgressDialog(this, "提示！", "正在获取" + ssid + "的ip...");
                    util.connDevice(ssid);
//                    util.DeviceList();
                }
//                util.DeviceList();
                break;
            case R.id.btn_on:
                if (util.isConnectedDev()) {
                    byte[] cmd = util.getCmdBytes(0);
                    String ip = util.getIp();
                    util.setBshortredid(v.getId());
                    if (cmd != null && cmd.length > 0) {
                        util.sendCmdCode(0x32, cmd, cmd.length, ip);
                    } else {
                        final byte[] buf = null;
                        util.mConnetListening.SendCmdCode(0x30, buf, 0, ip);
                    }
                } else {
                    CommUtils.showToast(this, "请先连接设备");
                }
//                util.setBshortredid(v.getId());
//                if(util.isBshortredflagA()){
////					CommUtils.showToast(ActivityMain.this, "open cmd : " + Arrays.toString(shortredcodeA) + " / ip : " + ip);
////                    CmdUtil.getInstance(ActivityMain.this).updateCmd(shortredcodeA,ip);
////					mConnetListening.SendCmdCode(0x32,shortredcodeA,shortredcodeA.length,ip);
//                    util.sendCmdCode(0x32, CmdUtil.shortredcodeA, CmdUtil.shortredcodeA.length, util.getIp());
//                }else{
//                    final byte[] buf = null;
////					if(ip == null||ip.equals(""))return;
////					mConnetListening.SendCmdCode(0x30,buf,0,ip);
//                    String ip = util.getIp();
//                    if (ip == null || ip.equals("")) return;
//                    util.mConnetListening.SendCmdCode(0x30, buf, 0, ip);
//                }
                break;
            case R.id.btn_off:
                if (util.isConnectedDev()) {
                    byte[] cmd = util.getCmdBytes(1);
                    String ip = util.getIp();
                    util.setBshortredid(v.getId());
                    if(/*util.isBshortredflagB()*/cmd != null && cmd.length > 0){
                        util.sendCmdCode(0x32, cmd, cmd.length, ip);
                    }else{
                        final byte[] buf = null;
                        util.mConnetListening.SendCmdCode(0x30, buf, 0, ip);
                    }
                } else {
                    CommUtils.showToast(this, "请先连接设备");
                }
                break;
            case R.id.btn_share:
                NetTool tool = new NetTool(this);
                tool.scan();
                break;
            default:
                break;
        }
    }

    private LoadingDialog loadingDialog;
    public void showLoadingDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }
}
