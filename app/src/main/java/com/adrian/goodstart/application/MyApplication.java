package com.adrian.goodstart.application;

import android.app.Activity;
import android.app.Application;

import com.adrian.goodstart.tool.DevSearchClientUtil;
import com.adrian.goodstart.tool.LogUtil;
import com.adrian.goodstart.tool.NetTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 16-4-2.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    private NetTool netTool;
    private DevSearchClientUtil searchClientUtil;

    private boolean noShare;
    private List<Activity> actList;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void sharePic() {
        if (netTool == null) {
            netTool = new NetTool(this);
        }
        if (noShare) {
            return;
        }
        LogUtil.e("APPLICATION", "share pic");

        if (searchClientUtil == null) {
            searchClientUtil = new DevSearchClientUtil(this);
        }
        searchClientUtil.searchDev();
    }

    public void setNoShare(boolean canShare) {
        this.noShare = canShare;
    }

    public boolean isNoShare() {
        return noShare;
    }

    public void addAct(Activity act) {
        if (actList == null) {
            actList = new ArrayList<Activity>();
        }
        actList.add(act);
    }

    public void delAct(Activity act) {
        if (actList != null && actList.contains(act)) {
            actList.remove(act);
        }
    }

    public void finishAct(Activity act) {
        if (actList != null && actList.contains(act) && !act.isFinishing()) {
            actList.remove(act);
            act.finish();
        }
    }

    public void finishAct(String actName) {
        if (actList != null) {
            for (Activity act :
                    actList) {
                String name = act.getLocalClassName();
                LogUtil.e("TAG", "act name : " + name);
                if (name.equals(actName) && !act.isFinishing()) {
                    act.finish();
                    actList.remove(act);
                }
            }
        }
    }

}
