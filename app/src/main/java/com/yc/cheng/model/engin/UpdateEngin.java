package com.yc.cheng.model.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.yc.cheng.constant.Config;
import com.yc.cheng.model.bean.InitInfo;
import com.yc.cheng.model.bean.UpdateInfo;

import rx.Observable;

public class UpdateEngin extends BaseEngin {

    public UpdateEngin(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return Config.UPDATE_URL;
    }

    public Observable<ResultInfo<UpdateInfo>> update(){
        return rxpost(new TypeReference<ResultInfo<UpdateInfo>>() {
        }.getType(), null, true, true, true);
    }
}
