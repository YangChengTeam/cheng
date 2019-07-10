package com.yc.cheng.helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youth.banner.loader.ImageLoader;

public class BannerImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        try {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            if(path.toString().indexOf("http") == -1){
                Glide.with(context).load(ContextCompat.getDrawable(context, (Integer)path)).apply(requestOptions).into(imageView);
            } else {
                Glide.with(context).load(path).apply(requestOptions).into(imageView);
            }
        } catch (Exception e){
            Log.e("BannerImageLoader",  e.getMessage());
        }
    }
}
