package com.yc.cheng.views.services;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yc.cheng.ChengApplication;
import com.yc.cheng.core.ChengSerialReadRes;
import com.yc.cheng.util.ChengUtils;
import com.yc.cheng.views.activitys.MainActivity;

import java.io.IOException;

import android_serialport_api.SerialPort;

public class ChengService extends BaseService {

    private static final String TAG = "ChengService";

    private SerialPort serialPort;
    private ReadThread mReadThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ChengServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ChengApplication chengApplication = (ChengApplication) getApplication();
        try {
            serialPort = chengApplication.getSerialPort();
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ChengServiceBinder extends Binder {
        public ChengService getService() {
            return ChengService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChengSerialReadRes.isRunning = false;
    }

    class ReadThread extends Thread {
        private ReadThread() {
        }

        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    byte[] buffer = new byte[64];
                    if (serialPort != null &&  serialPort.getInputStream() != null) {
                        int size = serialPort.getInputStream().read(buffer);
                        if (size > 0) {
                            byte[] data = new byte[size];
                            System.arraycopy(buffer, 0, data, 0, size);
                            ChengSerialReadRes.resolveData(data);
                        }
                    } else {
                        return;
                    }
                    if(!MainActivity.isFront && !UpdateService.isUpdate){
                        ChengUtils.open(getBaseContext());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
