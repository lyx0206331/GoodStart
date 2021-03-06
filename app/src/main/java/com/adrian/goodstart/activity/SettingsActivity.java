package com.adrian.goodstart.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.adrian.goodstart.R;
import com.adrian.goodstart.application.MyApplication;
import com.adrian.goodstart.tool.CmdCheck;
import com.adrian.goodstart.tool.CommUtils;
import com.adrian.goodstart.tool.ConnetListening;
import com.adrian.goodstart.tool.DataUtil;
import com.adrian.goodstart.tool.LogUtil;
import com.adrian.goodstart.tool.SharePrefUtil;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private Spinner mDevSpinner;
    private EditText mDevPwdET;
    private Button mConnDevBtn;

    private Spinner mWifiSpinner;
    private EditText mWifiPwdET;
    private Button mWifiConnDevBtn;

    private String[] devList;
    private String[] wifiList;

    private CmdCheck mCmdCheck;
    private ProgressDialog mProgressDialog = null;
    private LocalReceiver receiver;
    private String sname = "";
    private String DeviceName = "";
    private String DevicePassword = "";
    private String NetName = "";
    private String NetPassword = "";

    private AlertDialog connDialog;

//    private DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        MyApplication.getInstance().setNoShare(false);
        super.onDestroy();
    }

    @Override
    protected void initVariables() {

        MyApplication.getInstance().addAct(this);

        MyApplication.getInstance().setNoShare(true);
        ActivityMain.setHandler(mHandler);
    }

    private void initData() {
        List<ScanResult> list = /*MainActivity.util*/ActivityMain.mConnetListening.ConnectWifiScanResult();
        if (list == null || list.size() == 0) {
            CommUtils.showToast(this, "无可连接wifi");
            return;
        }
        List<String> devs = new ArrayList<>();
        List<String> wifis = new ArrayList<>();
        devs.add("请选择设备");
        wifis.add("请选择本地WIFI");
        for (ScanResult result :
                list) {
            if (result.SSID.contains("IYK")) {
                devs.add(result.SSID);
            } else {
                wifis.add(result.SSID);
            }
        }
        devList = devs.toArray(new String[devs.size()]);
        wifiList = wifis.toArray(new String[wifis.size()]);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_settings);
        mDevSpinner = (Spinner) findViewById(R.id.spinner_dev);
        mDevPwdET = (EditText) findViewById(R.id.et_dev_pwd);
        mConnDevBtn = (Button) findViewById(R.id.btn_conn_dev);
        mWifiSpinner = (Spinner) findViewById(R.id.spinner_wifi);
        mWifiPwdET = (EditText) findViewById(R.id.et_wifi_pwd);
        mWifiConnDevBtn = (Button) findViewById(R.id.btn_dev_conn_wifi);

        mConnDevBtn.setOnClickListener(this);
        mWifiConnDevBtn.setOnClickListener(this);


        initData();
        if (devList != null && devList.length > 0) {
            final ArrayAdapter<String> devAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, devList);
            devAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mDevSpinner.setAdapter(devAdapter);
            mDevSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
//                        CommUtils.showToast(SettingsActivity.this, "请选择设备");
                    } else {
                        DeviceName = devList[position];
//                        MainActivity.util.setSsid(DeviceName);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        if (wifiList != null && wifiList.length > 0) {
            ArrayAdapter<String> wifiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wifiList);
            wifiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mWifiSpinner.setAdapter(wifiAdapter);
            mWifiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
//                        CommUtils.showToast(SettingsActivity.this, "请选择网络");
                    } else {
                        NetName = wifiList[position];
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        showConnDialog();
    }

    private void showConnDialog() {
        if (devList == null || devList.length < 2) {
            CommUtils.showToast(this, "未检测到远程遥控设备");
            return;
        }
        if (connDialog == null) {
            connDialog = new AlertDialog.Builder(this).setTitle("网络连接").setMessage("请先到网络设置页连接网络" + devList[1])
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
                        }
                    }).create();
        }
        connDialog.show();
    }

    @Override
    protected void loadData() {
        reg();
    }

    public void reg() {
        receiver = new LocalReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_conn_dev:
                DevicePassword = mDevPwdET.getText().toString();
                if (TextUtils.isEmpty(DeviceName)) {
                    CommUtils.showToast(this, "请选择设备");
                } else if (TextUtils.isEmpty(DevicePassword)) {
                    mDevPwdET.setError("请输入设备密码");
                } else {
                    if (/*MainActivity.util*/ActivityMain.mConnetListening.getConnectWifiSSID().contains(DeviceName)) {
                        return;
                    } else {
                        /*MainActivity.util*/
                        ActivityMain.mConnetListening.ConnectWifi(DeviceName, DevicePassword, 3);
                        if (mProgressDialog == null) {
                            mProgressDialog = ProgressDialog.show(this, "提示！", "正在连接设备：" + DeviceName + "...", true, true);
                            mProgressDialog.setCanceledOnTouchOutside(false);
                        }
                        mProgressDialog.setMessage("正在连接设备：" + DeviceName + "...");
                        mProgressDialog.show();
                    }
                }
                break;
            case R.id.btn_dev_conn_wifi:
                NetPassword = mWifiPwdET.getText().toString();
                if (TextUtils.isEmpty(NetName)) {
                    CommUtils.showToast(this, "请选择网络");
                } else if (TextUtils.isEmpty(NetPassword)) {
                    mWifiPwdET.setError("请输入网络密码");
                } else if (!CommUtils.getWifiName(this).contains("IYK")) {
                    showConnDialog();
                } else {
                    if (/*MainActivity.util*/ActivityMain.mConnetListening.getConnectWifiSSID().contains(DeviceName)) {
                        if (mProgressDialog == null) {
                            mProgressDialog = ProgressDialog.show(this, "提示！", "正在连接网络：" + NetName + "...", true, true);
                            mProgressDialog.setCanceledOnTouchOutside(false);
                        }
                        mProgressDialog.setMessage("正在连接网络：" + NetName + "...");
                        mProgressDialog.show();
                        LinkNet();
                        Log.e("ActivityNet", "LinkNet-->" + NetName);
                        //return;
                    }
                }
                break;
            default:
                break;
        }
    }

    private void LinkNet() {
        byte[] buf = new byte[48];
        byte[] buf1 = new byte[48];
        int i = 0;
        buf[i++] = (byte) 0x22;
        buf1 = NetName.getBytes();
        for (int j = 0; j < NetName.length(); j++) {
            buf[i++] = buf1[j];
        }
        buf[i++] = (byte) 0x22;
        buf[i++] = (byte) 0x2c;
        buf[i++] = (byte) 0x22;
        buf1 = NetPassword.getBytes();
        for (int j = 0; j < NetPassword.length(); j++) {
            buf[i++] = buf1[j];
        }
        buf[i++] = (byte) 0x22;
        /*MainActivity.util*/
        ActivityMain.mConnetListening.SendCmdCode(2, buf, buf.length, "255.255.255.255");
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, intent.getAction(), 1).show();

            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo.getState() != android.net.NetworkInfo.State.CONNECTED) {
                Log.e("MainActivity", "!!!wifiInfo.State.CONNECTED");

            } else if (wifiInfo.getState() == android.net.NetworkInfo.State.CONNECTED) {
                Log.e("MainActivity", "wifiInfo.State.CONNECTED");
                if (/*MainActivity.util*/ActivityMain.mConnetListening.getConnectWifiSSID().contains(DeviceName)) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                }
            }

        }

    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConnetListening.LISTENING_MESSAGE_STATE:
                    switch (msg.arg1) {
                        case ConnetListening.MESSAGE_READSUCCESS:
                            Log.e("Activity_Timeclock_Add", "ConnetListening.MESSAGE_READSUCCESS");
                            mCmdCheck = new CmdCheck((byte[]) msg.obj, msg.arg2);
                            if (mCmdCheck.getCmd() == (byte) 0x82) {
                                if (mCmdCheck.getCmdSetState() == 1) {
                                    SharePrefUtil.setWifiSsid(SettingsActivity.this, NetName);
//                                    mProgressDialog.cancel();
//                                    mProgressDialog.dismiss();
                                    ActivityMain.mConnetListening.ConnectWifi(NetName, NetPassword, 3);//切换到局域网；
                                    LogUtil.e("TAG", "切换到局域网");
//                                    finish();
                                } else if (mCmdCheck.getCmdSetState() == 2) {
                                    mProgressDialog.cancel();
                                    mProgressDialog.dismiss();
                                    Toast toast = Toast.makeText(SettingsActivity.this, "WIFI连接失败！", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                            break;
                        case ConnetListening.MESSAGE_READFAILD:
                            break;
                    }
                    break;
            }
        }
    };
}
