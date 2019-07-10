package com.yc.cheng.views.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yc.cheng.views.activitys.LoadingActivity;

public class BootReceiver extends BroadcastReceiver {

    public static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(action_boot)){
            Intent mainIntent = new Intent(context, LoadingActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
    }
}
