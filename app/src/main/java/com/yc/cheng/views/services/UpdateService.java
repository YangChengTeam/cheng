package com.yc.cheng.views.services;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.VUiKit;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.yc.cheng.R;
import com.yc.cheng.constant.Config;
import com.yc.cheng.model.bean.UpdateInfo;
import com.yc.cheng.model.engin.UpdateEngin;
import com.yc.cheng.util.ChengUtils;
import com.yc.cheng.views.activitys.MainActivity;

import rx.Subscriber;

public class UpdateService extends BaseService {
    private Handler handler = new Handler();
    private static final String TAG = "UpdateService";
    public static boolean isUpdate;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new UpdateServiceBinder();
    }

    public class UpdateServiceBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updateEngin();
        updateLoop();
    }

    private void updateLoop(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateEngin();
            }
        }, 1000 * 60 * 30 );
    }

    private void updateEngin(){
        new UpdateEngin(getBaseContext()).update().subscribe(new Subscriber<ResultInfo<UpdateInfo>>() {
            @Override
            public void onCompleted() {
                updateLoop();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResultInfo<UpdateInfo> updateInfoResultInfo) {
                if (updateInfoResultInfo != null && updateInfoResultInfo.getCode() == 1) {
                    UpdateInfo updateInfo = updateInfoResultInfo.getData();
                    if(updateInfo.getVersionCode() > GoagalInfo.get().packageInfo.versionCode){
                        download(updateInfo.getDownUrl());
                    }
                }
            }
        });
    }

    private ProgressDialog progressDialog;
    private void download(String url){
        MainActivity mainActivity = MainActivity.getMainActivity();
        if(mainActivity == null) return;
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(getString(R.string.app_name)+ "更新");
        progressDialog.setMessage("\n\n正在下载...");
        progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setCanceledOnTouchOutside(false);
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
                                progressDialog.setProgress((int)(((float)soFarBytes/totalBytes) * 100));
                            }
                        });
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        VUiKit.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                            }
                        });
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                        VUiKit.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.show();
                            }
                        });
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        VUiKit.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                                isUpdate = true;
                                ChengUtils.openUpdate(mainActivity, Config.PATH + ChengUtils.md5(url));
                            }
                        });

                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                    }
                }).start();
    }
}
