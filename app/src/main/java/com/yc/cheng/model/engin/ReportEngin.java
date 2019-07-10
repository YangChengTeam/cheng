package com.yc.cheng.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.yc.cheng.constant.Config;
import com.yc.cheng.model.bean.InitInfo;

import java.util.HashMap;

import rx.Observable;

public class ReportEngin extends BaseEngin {

    public ReportEngin(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return Config.REPORT_URL;
    }

    public Observable<ResultInfo<InitInfo>> report(String  longitude,String latitude, String location){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("longitude", longitude);
        hashMap.put("latitude", latitude);
        hashMap.put("location", location);
        return rxpost(new TypeReference<ResultInfo<InitInfo>>() {
        }.getType(), hashMap, true, true, true);
    }
}
