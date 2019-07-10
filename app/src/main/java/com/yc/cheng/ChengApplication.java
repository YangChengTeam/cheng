package com.yc.cheng;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.net.contains.HttpConfig;
import com.kk.utils.LogUtil;
import com.kk.utils.security.Encrypt;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.tencent.mmkv.MMKV;
import com.yr.huajian.utils.JniUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

public class ChengApplication extends Application {

    private HttpProxyCacheServer proxy;
    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (this.mSerialPort == null) {
            this.mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 9600, 0);
        }
        return this.mSerialPort;
    }

    public void closeSerialPort() {
        if (this.mSerialPort != null) {
            this.mSerialPort.close();
            this.mSerialPort = null;
        }
    }

    public void onCreate() {
        super.onCreate();
        MMKV.initialize(this);

        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                ))
                .commit();

        new Thread(new Runnable() {
            @Override
            public void run() {
                GoagalInfo.get().init(getApplicationContext());
                HttpConfig.setPublickey(
                        "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAq1wNiX3iQt+Q7juXZDNR\n" +
                        "Eq2jGqx+2pXM4ddoZ1rkHb3XJFhrBguI/R11IfmTioPlTnheJqYkJf0NGzzxW2t1\n" +
                        "nDKwbjoZ+d7UMehCDV44+FQMtzhRAFjmcQIXn6AaL2bkJFzHvoTtYPqgqgT5V4L6\n" +
                        "+DhLSuPSwIVAC1aw1+iUk3jbg3ETzERSS6LDHTRi2ng7rpKAeHKeJ2RtbrcetCxv\n" +
                        "YF+6QabnJhZGtr6cvp9CtFv5bSc2JsCqbJbsDGM6OPAjQjtpmImxQiXcI1gko8WP\n" +
                        "+k1nx9GPJBhtdAXORRGRoHA8fUCveAJPDw1jSF3lBDf+1BHx+XeVX4/sVybd5Rn3\n" +
                        "IE21UeuF+kbmwULJKUDzQNIwlXA+k4faRhdKeFCOeqldozwhP+575L/vVlyvxx/M\n" +
                        "UJdA4vUziyO1l/IQEGzJ7b4AWfJ6sQEKDjODuLM+DM9MAuYddFnNfKj8XVi3jx9y\n" +
                        "0OOAb/4Rb3UPeOUF9R4Sr0nLmL/1ITL8/9rJaue/e/D7H4xfQNbCtSTPhsa/+UOt\n" +
                        "j3AQsNUjqkoLMXm7vtXEIshXEm4mlmMl98LsXyK3B6lMiV7jO4Vyp8muga8I/nH3\n" +
                        "Snw5e86AHSZdnbQcLTDx9sgqN2mSL3MqLp9oiL4KGxNdNdt8EunGRycTsj09o7oz\n" +
                        "Lfxf+/8xTiWygyUTThX+/GUCAwEAAQ==\n"
                        );
            }
        }).start();


    }

    public static HttpProxyCacheServer getProxy(Context context) {
        ChengApplication app = (ChengApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
        closeSerialPort();
    }


}
