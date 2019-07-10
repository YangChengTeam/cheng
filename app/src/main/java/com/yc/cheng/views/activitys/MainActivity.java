package com.yc.cheng.views.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.security.Base64;
import com.kk.utils.security.Encrypt;
import com.tencent.mmkv.MMKV;
import com.yc.cheng.helper.BannerImageLoader;
import com.yc.cheng.ChengApplication;
import com.yc.cheng.core.ChengSerialReadRes;
import com.yc.cheng.views.services.ChengService;
import com.yc.cheng.views.services.LocationService;
import com.yc.cheng.core.QRCodeEncoder;
import com.yc.cheng.R;
import com.yc.cheng.constant.Config;
import com.yc.cheng.model.bean.InitInfo;
import com.yc.cheng.model.engin.InitEngin;
import com.yc.cheng.util.ChengUtils;
import com.yc.cheng.views.services.UpdateService;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import rx.Subscriber;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private final Handler handler = new Handler();

    private RelativeLayout leftBgView;
    private RelativeLayout resultBgView;
    private TextView successCountDownTimeTextView;
    private ImageView qrcodeImageView;

    private TextView timeTextView;
    private TextView dateTextView;
    private TextView descTextView;
    private TextView processTextView;

    private LinearLayout processBgView;
    private View processBgView2;

    private Banner mBanner;
    private VideoView mVideoview;

    private InitEngin initEngin;
    private InitInfo initInfo;

    private boolean isSuccess;
    public static boolean isFront ;

    private static MainActivity mainActivity;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        leftBgView      = findViewById(R.id.ll_bg_left);
        resultBgView    = findViewById(R.id.rl_bg_result);
        successCountDownTimeTextView = findViewById(R.id.tv_success_time);

        timeTextView = findViewById(R.id.tv_time);
        dateTextView = findViewById(R.id.tv_date);

        descTextView = findViewById(R.id.tv_desc);
        processTextView = findViewById(R.id.tv_process);
        processBgView = findViewById(R.id.ll_bg_process);
        processBgView2 = findViewById(R.id.v_bg_process2);
        processTextView.setText("倒计时: "+timeout/2+"s");

        mBanner = findViewById(R.id.banner);
        mVideoview = findViewById(R.id.videoplayer);

        qrcodeImageView = findViewById(R.id.iv_qrcode);

        initEngin = new InitEngin(this);

        isFront = true;
        startService(new Intent(this, ChengService.class));
        startService(new Intent(this, LocationService.class));
        startService(new Intent(this, UpdateService.class));

        Timeloop();
        chengLoop();

        String initInfoStr = MMKV.defaultMMKV().getString(Config.INIT_INFO_KEY, "");
        if(!TextUtils.isEmpty(initInfoStr)){
            try {
                initInfo = JSON.parseObject(initInfoStr, InitInfo.class);
                if(initInfo != null){
                    resetResources();
                }
            }catch (Exception e){
                Log.d(TAG, "initInfo转换失败");
            }
        } else {
            showDefaultAd();
        }
        enginLoop();

        getInitInfo();

    }

    private long[] doIntervals = new long[]{30_000, 5000, 5000, 5000} ;
    private long[] doTimes = new long[]{0, 0, 0, 0} ;
    protected void interval(Runnable runnable, int i){
        long doTimeEnd = System.currentTimeMillis();
        if(doTimeEnd - doTimes[i] > doIntervals[i]){
            runnable.run();
            doTimes[i] = doTimeEnd;
        }
    }

    private String[] voices = new String []{"cicleplay.mp3",  "onweight.mp3", "tizhitip.mp3", "weightok.mp3",};
    private final int TIMEOUT = 60;
    private int timeout = TIMEOUT;
    protected void chengLoop(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(isSuccess){
                    return;
                }

                if(ChengSerialReadRes.isStartSignal){
                    timeout--;
                    if(timeout % 2 == 0){
                        processTextView.setText("倒计时: "+timeout/2+"s");
                        Log.i(TAG, "倒计时:" + timeout/2);
                        int width = (int) (((float)(timeout)) / TIMEOUT * ChengUtils.dip2px(MainActivity.this, 180));
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) processBgView2.getLayoutParams();
                        layoutParams.width = width;
                        processBgView2.setLayoutParams(layoutParams);
                    }

                    if(timeout <= 0){
                        timeout = TIMEOUT;
                        Log.i(TAG, "测量超时");
                        resetInfo();
                    }
                }

                if(!TextUtils.isEmpty(ChengSerialReadRes.impedance)){
                    interval(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "测量完成" + ChengSerialReadRes.weight + "-" + ChengSerialReadRes.impedance);
                            success();
                        }
                    }, 2);
                }
                else if(!TextUtils.isEmpty(ChengSerialReadRes.weight) && Float.valueOf(ChengSerialReadRes.weight) > 0){
                    interval(new Runnable() {
                        @Override
                        public void run() {
                            leftBgView.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_left3));
                            descTextView.setVisibility(View.VISIBLE);
                            processTextView.setVisibility(View.VISIBLE);
                            processBgView.setVisibility(View.VISIBLE);
                            descTextView.setText("正在测量体脂...");
                            playAudio(2);
                        }
                    }, 3);
                }
                else if(ChengSerialReadRes.isStartSignal){
                    interval(new Runnable() {
                        @Override
                        public void run() {
                            leftBgView.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_left2));
                            descTextView.setVisibility(View.VISIBLE);
                            processTextView.setVisibility(View.VISIBLE);
                            processBgView.setVisibility(View.VISIBLE);
                            descTextView.setText("正在测量体重...");
                            playAudio(1);
                        }
                    }, 1);
                }
                else if(ChengSerialReadRes.isHeartbeatSignal){
                    interval(new Runnable() {
                        @Override
                        public void run() {
                            timeout = TIMEOUT;
                            playAudio(0);
                        }
                    }, 0);
                } else {
                    if(ChengSerialReadRes.isRunning){
                        Log.i(TAG, "人还没有下秤或没有心跳信号");
                    } else {
                        Log.i(TAG, "服务没有开始或停止");
                        startService(new Intent(MainActivity.this, ChengService.class));
                    }
                }
                chengLoop();
            }
        }, 500);
    }


    private void resetInfo(){
        timeout = TIMEOUT;
        processTextView.setText("倒计时: "+timeout/2+"s");

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)processBgView2.getLayoutParams();
        layoutParams.width = ChengUtils.dip2px(this, 180);
        processBgView2.setLayoutParams(layoutParams);
        processTextView.setVisibility(View.GONE);
        processBgView.setVisibility(View.GONE);
        descTextView.setVisibility(View.GONE);
        leftBgView.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_left));
        ChengSerialReadRes.isStartSignal = false;
        ChengSerialReadRes.impedance = "";
        ChengSerialReadRes.weight = "";
    }

    private void Timeloop(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateformat = new SimpleDateFormat("EEEE yyyy-MM-dd");
        timeTextView.setText(timeformat.format(calendar.getTime()));
        dateTextView.setText(dateformat.format(calendar.getTime()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateformat = new SimpleDateFormat("EEEE yyyy-MM-dd");
                timeTextView.setText(timeformat.format(calendar.getTime()));
                dateTextView.setText(dateformat.format(calendar.getTime()));
                Timeloop();
            }
        }, 1000);
    }


    int successCountDownTime = 30;

    private void success(){
        try {
            int PADDING_SIZE_MIN = ChengUtils.dip2px(this, 20);
            int wh = ChengUtils.dip2px(this, 240 ) + PADDING_SIZE_MIN;

            Bitmap qrcodeBitmap = QRCodeEncoder.encodeAsBitmap(genQrcodeInfo(), wh, wh, PADDING_SIZE_MIN/2);
            qrcodeImageView.setImageBitmap(qrcodeBitmap);
            isSuccess = true;
            resultBgView.setVisibility(View.VISIBLE);
            resultBgView.bringToFront();
            successCountDownTime = 30;
            successCountDownTimeTextView.setText(successCountDownTime+"");
            successLoop();
            playAudio(3);
            resetInfo();
        } catch (Exception e){

        }
    }

    private void successLoop(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(successCountDownTime <= 1) {
                    resultBgView.setVisibility(View.GONE);
                    isSuccess = false;
                    chengLoop();
                    return;
                }
                successCountDownTimeTextView.setText((--successCountDownTime > 9) ? successCountDownTime + "": "0" + successCountDownTime);
                successLoop();
            }
        }, 1000);
    }

    private String genQrcodeInfo(){
        String data= Base64.encode(Encrypt.encode((ChengSerialReadRes.weight + "-" + ChengSerialReadRes.impedance) + "-" + ChengUtils.getDeviceId(this)).getBytes());
        return Config.QRCODE_JUMP_URL + "?data=" + data;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    private void playVideo(String url){
        mVideoview.setVisibility(View.VISIBLE);
        mBanner.setVisibility(View.GONE);

        HttpProxyCacheServer proxy = ChengApplication.getProxy(this);
        String proxyUrl = proxy.getProxyUrl(Uri.parse(url).toString());
        mVideoview.setVideoPath(proxyUrl);
        mVideoview.requestFocus();
        mVideoview.start();
        mVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        mBanner.stopAutoPlay();
    }

    private void showImages(List<?> images){
        mVideoview.setVisibility(View.GONE);
        mBanner.setVisibility(View.VISIBLE);
        mBanner.setFocusable(false);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.isAutoPlay(true)
                .setDelayTime(5000)
                .setImageLoader(new BannerImageLoader())
                .setImages(images)
                .start();
        if(mVideoview.isPlaying()){
            mVideoview.resume();
            mVideoview.pause();
        }
    }

    public static final int ENGIN_LOOP_TIME = 60 * 5  * 1000;
    private void enginLoop(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getInitInfo();
                Log.d(MainActivity.TAG, "enginLoop");
                enginLoop();
            }
        }, ENGIN_LOOP_TIME);
    }

    private void getInitInfo(){
        if(ChengUtils.isOnline()) {
            initEngin.init().subscribe(new Subscriber<ResultInfo<InitInfo>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(ResultInfo<InitInfo> initInfoResultInfo) {
                    if (initInfoResultInfo != null && initInfoResultInfo.getCode() == 1) {
                        InitInfo tmpInitInfo = initInfoResultInfo.getData();
                        if (tmpInitInfo != null && initInfo != null && initInfo.getUpdate_time() != null && initInfo.getUpdate_time().equals(tmpInitInfo.getUpdate_time())) {
                            Log.d(MainActivity.TAG, "信息未更新");
                        }
                        else if(tmpInitInfo != null){
                            initInfo = tmpInitInfo;
                            Log.d(MainActivity.TAG, "信息已更新");
                            MMKV.defaultMMKV().putString(Config.INIT_INFO_KEY, JSON.toJSONString(initInfo));
                            resetResources();
                        }
                    }
                }
            });
        }
    }

    private void showDefaultAd(){
        List<Object> images = new ArrayList<>();
        images.add(R.drawable.banner1);
        images.add(R.drawable.banner2);
        images.add(R.drawable.banner3);
        images.add(R.drawable.banner4);
        showImages(images);
    }

    private void resetResources(){
        if(initInfo.getType() == 1){
            showImages(initInfo.getPics());
        } else if(initInfo.getType() == 2){
            playVideo(initInfo.getVideo());
        } else {
            showDefaultAd();
        }

        if(initInfo.getVolum_value() != 0){
            ChengUtils.setVolum(initInfo.getVolum_value());
            ChengUtils.setMaxVolume(this);
        }

        if(initInfo.getBrightness_value() != 0){
            ChengUtils.setBrightnessValue(initInfo.getBrightness_value());
            ChengUtils.setScreenBrightness(this);
        }

        if(initInfo.getVoices() != null){
            for(int i = 0;  i < initInfo.getVoices().size(); i++){
                ChengUtils.download(initInfo.getVoices().get(i));
            }
        }

        if(!TextUtils.isEmpty(initInfo.getQrcode_jump_url())){
            Config.QRCODE_JUMP_URL = initInfo.getQrcode_jump_url();
        }

        if(!TextUtils.isEmpty(initInfo.getQrcode_logo_url())){
            Glide.with(this).load(initInfo.getQrcode_logo_url()).into((ImageView) findViewById(R.id.iv_logo));
        }
    }

    private void playAudio(int i){
        if(initInfo != null && initInfo.getVoices() != null &&initInfo.getVoices().size() > i) {
            File file = new File(Config.PATH + ChengUtils.md5(initInfo.getVoices().get(i)));
            if(file.exists()) {
                ChengUtils.playAudio2(this, file.getAbsolutePath());
            } else {
                ChengUtils.playAudio(this,  voices[i]);
            }
        } else {
            ChengUtils.playAudio(this,  voices[i]);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
        UpdateService.isUpdate = false;
        mediaStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isFront = false;
        mediaStop();
    }

    private void mediaStart(){
        if(!mVideoview.isPlaying() && mVideoview.getVisibility() == View.VISIBLE){
            mVideoview.start();
        }

        if(mBanner.getVisibility() == View.VISIBLE){
            mBanner.startAutoPlay();
        }
    }

    private void mediaStop(){
        if(mVideoview.isPlaying() && mVideoview.getVisibility() == View.VISIBLE){
            mVideoview.resume();
            mVideoview.pause();
        }

        if(mBanner.getVisibility() == View.VISIBLE){
            mBanner.stopAutoPlay();
        }
    }

}
