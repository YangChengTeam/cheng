package com.yc.cheng.core;

import android.util.Log;

public class ChengSerialReadRes {

    private static final String TAG = "ChengSerialReadRes";

    public static boolean isRunning = false;
    public static String weight = "";
    public static String impedance = "";

    public static boolean isHeartbeatSignal = false;
    public static boolean isStartSignal = false;

    public static void resolveData(byte[] data) {
        isRunning = true;

        if(toHexString(data[0]).equals("0xA5") && toHexString(data[1]).equals("0x1") && toHexString(data[2]).equals("0xFF")){
            Log.d(TAG, "心跳信号");

            isHeartbeatSignal =true;
        } else if(toHexString(data[0]).equals("0xA5") && toHexString(data[1]).equals("0x2") && toHexString(data[2]).equals("0xFF")){
            Log.d(TAG, "启动测量信号");

            isStartSignal = true;
            isHeartbeatSignal = false;
            ChengSerialReadRes.weight = "";
            ChengSerialReadRes.impedance = "";

        } else if(toHexString(data[0]).equals("0xA5") && toHexString(data[1]).equals("0x4")){
            if(checkSum(data)){
                int temp = Integer.valueOf(Integer.toHexString(data[2] & 0xFF)+""+Integer.toHexString(data[3] & 0xFF), 16);
                ChengSerialReadRes.weight = (temp / 100) + "." + (temp % 100);
                Log.d(TAG, "体重数据" + ChengSerialReadRes.weight);
            }
        } else if(toHexString(data[0]).equals("0xA5") && toHexString(data[1]).equals("0x5")){
            if(checkSum(data)){
                ChengSerialReadRes.impedance = Integer.valueOf(Integer.toHexString(data[2] & 0xFF)+""+Integer.toHexString(data[3] & 0xFF), 16) + "";
                Log.d(TAG, "阻抗数据" + ChengSerialReadRes.impedance);
            }
        }
    }

    private static boolean checkSum(byte[] data){
        if(data.length > 4 && (data[0] ^ data[1] ^ data[2] ^ data[3]) == data[4])
             return  true;
        return false;
    }

    private static String toHexString(byte bt){
        return "0x"+ Integer.toHexString(bt & 0xFF).toUpperCase();
    }
}
