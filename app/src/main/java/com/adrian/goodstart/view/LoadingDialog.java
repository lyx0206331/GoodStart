package com.adrian.goodstart.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.adrian.goodstart.R;

/**
 * Created by adrian on 16-3-28.
 */
public class LoadingDialog extends Dialog {

    private Context context;
    private TextView mMessageTV;

    public LoadingDialog(Context context) {
        super(context, R.style.Dialog);
        init(context);
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setContentView(R.layout.layout_loading);
        mMessageTV = (TextView) findViewById(R.id.tv_message);
        setCanceledOnTouchOutside(false);
    }

    public void setMessage(String message) {
        if (message != null) {
            mMessageTV.setText(message);
        }
    }
}
