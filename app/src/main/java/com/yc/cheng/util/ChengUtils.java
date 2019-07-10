package com.yc.cheng.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.utils.VUiKit;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.yc.cheng.constant.Config;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ChengUtils {

    private static MediaPlayer player;
    private static AudioManager mAudioManager;

    private static float volum = 0.2f;
    private static float  brightnessValue = 1f;

    public static void setVolum(float volum) {
        ChengUtils.volum = volum;
    }

    public static void setBrightnessValue(float brightnessValue) {
        ChengUtils.brightnessValue = brightnessValue;
    }

    public static void setMaxVolume(Context context){
        if(mAudioManager == null) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC , (int)(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                * volum), 0);

    }


    public static final int REQUEST_CODE = 1;

    public static boolean checkPermission(Activity context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(context.checkSelfPermission(permission) == PackageManager
                    .PERMISSION_GRANTED)) {
                if (context.shouldShowRequestPermissionRationale(permission)) {
                }
                requestMultiplePermissions(context, permission);
                return false;
            }
            return true;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void requestMultiplePermissions(Activity context, String permission) {
        String[] permissions = {permission};
        context.requestPermissions(permissions, REQUEST_CODE);
    }

    public static void setScreenBrightness(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
            Settings.System.putInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS, (int)(255 * brightnessValue));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }




    public static void playAudio(Context context, String name){
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor afd = assetManager.openFd(name);
            if(player != null) {
                player.stop();
                player.reset();
                player.release();
                player = null;
            }
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playAudio2(Context context, String path){
        try {
            if(player != null) {
                player.stop();
                player.reset();
                player.release();
                player = null;
            }
            player = new MediaPlayer();
            player.setDataSource(path);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 114.114.114.114");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public static void download(String url){
        FileDownloader.getImpl().create(url)
                .setPath(Config.PATH + ChengUtils.md5(url), false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        VUiKit.post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        Log.d("download", task.getUrl());

                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                    }
                }).start();
    }


    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void openUpdate(Context context, String path){
        try {
            Intent intent = new Intent();
            ComponentName component = new ComponentName("com.yc.updatecheng", "com.yc.updatecheng.MainActivity");
            intent.putExtra("Path", path);
            if(GoagalInfo.get().packageInfo != null) {
                intent.putExtra("VersionCode", GoagalInfo.get().packageInfo.versionCode);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(component);
            context.startActivity(intent);
        }catch (Exception e){

        }
    }

    public static void open(Context context){
        Intent intent = new Intent();
        ComponentName component = new ComponentName("com.yc.cheng","com.yc.cheng.views.activitys.MainActivity");
        intent.setComponent(component);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
