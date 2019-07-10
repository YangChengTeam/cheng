package com.yr.huajian.utils;

public class JniUtils {
    public static native String getEncryptKey();

    public static native String getWXKey();

    static {
        System.loadLibrary("MyJni");
    }

}
