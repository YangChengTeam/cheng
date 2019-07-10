package com.yc.cheng.views.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.net.contains.HttpConfig;
import com.tencent.mmkv.MMKV;
import com.yc.cheng.R;
import com.yc.cheng.constant.Config;
import com.yc.cheng.model.bean.InitInfo;
import com.yc.cheng.util.ChengUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends BaseActivity {

    public static final String TAG = "LoadingActivity";

    private final Handler handler = new Handler();

    private String welcomeMp3Name = "welcome.mp3";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        setDefaultParams();
        ChengUtils.setMaxVolume(this);

        String initInfoStr = MMKV.defaultMMKV().getString(Config.INIT_INFO_KEY, "");

        if (!TextUtils.isEmpty(initInfoStr)) {
            try {
                InitInfo initInfo = JSON.parseObject(initInfoStr, InitInfo.class);

                if(initInfo.getVolum_value() != 0){
                    ChengUtils.setVolum(initInfo.getVolum_value());
                    ChengUtils.setMaxVolume(this);
                }

                if(initInfo.getBrightness_value() != 0){
                    ChengUtils.setBrightnessValue(initInfo.getBrightness_value());
                    ChengUtils.setScreenBrightness(this);
                }

            } catch (Exception e) {
                Log.d(TAG, "initInfo转换失败");
            }
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }


    private void setDefaultParams() {
        //设置http默认参数
        Map<String, String> params = new HashMap<>();
        if (GoagalInfo.get().packageInfo != null) {
            params.put("app_version", GoagalInfo.get().packageInfo.versionCode + "");
        }
        String imeil = GoagalInfo.get().uuid;
        if (ChengUtils.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            imeil = ChengUtils.getDeviceId(this);
        }
        params.put("imeil", imeil);
        HttpConfig.setDefaultParams(params);
    }

}
