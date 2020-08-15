package com.dong.expense.ui.activity;

import com.dong.expense.R;
import com.dong.expense.logic.ApplicationPool;
import com.dong.expense.utils.SharedUtil;
import com.dong.expense.utils.StringUtil;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.umeng.analytics.MobclickAgent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends Activity {

    private static final String TAG = WelcomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		ApplicationPool.getInstance().addActivity(this);
        setContentView(R.layout.activity_welcome);
        initView();
        intoNext();
    }

    private void initView() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        String rationale = "Please provide permissions";
        Permissions.Options options =
                new Permissions.Options().setRationaleDialogTitle("Info").setSettingsDialogTitle("Warning");
        Permissions.check(this, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {

            }
        });
    }

    private void intoNext() {
        Intent intent = new Intent();
        if (SharedUtil.isGesturePasswork(this)) {
            String passwork = SharedUtil.getGesturePasswork(this);
            if (StringUtil.isNotNull(passwork)) {
                intent.setClass(this, GestureLoginActivity.class);
            } else {
                intent.setClass(this, MainActivity.class);
            }
        } else {
            intent.setClass(this, GestureSetActivity.class);
        }
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }
}
