package com.yc.cheng.views.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yc.cheng.R;
import com.yc.cheng.model.engin.ReportEngin;
import com.yc.cheng.views.activitys.MainActivity;


public class LocationService extends BaseService {
    private static final String TAG = "LocationService";

    private LocationClient mLocationClient;
    private BDAbstractLocationListener locationListener;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocationServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationListener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                Log.d(TAG, bdLocation.getLatitude() + "-" +  bdLocation.getLongitude()+"-" + bdLocation.getLocationDescribe());
                new ReportEngin(getApplicationContext()).report(bdLocation.getLongitude()+"", bdLocation.getLatitude()+"", bdLocation.getLocationDescribe()).subscribe();
            }
        };
        this.mLocationClient = new LocationClient(getApplicationContext());
        this.mLocationClient.registerLocationListener(locationListener);
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClientOption.setCoorType(BDLocation.BDLOCATION_GCJ02_TO_BD09LL);
        locationClientOption.setScanSpan(600000);
        locationClientOption.setIsNeedAddress(true);
        locationClientOption.setOpenGps(true);
        locationClientOption.setIsNeedLocationDescribe(true);
        locationClientOption.setIsNeedLocationPoiList(true);
        locationClientOption.setIgnoreKillProcess(false);
        locationClientOption.SetIgnoreCacheException(false);
        locationClientOption.setEnableSimulateGps(false);
        this.mLocationClient.setLocOption(locationClientOption);
        this.mLocationClient.start();
    }

    public class LocationServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
}
