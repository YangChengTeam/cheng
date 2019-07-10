package com.yc.cheng;


import org.apache.commons.codec.binary.Base64;

import java.net.URLDecoder;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


public class EncryptUtils {


    public static void main(String[] args){
        System.out.print(EncryptUtils.encryptDESString("{\"uid\":\"62550319\",\"source\":\"2\",\"packageName\":\"com.yr.huajian\",\"token\":\"884f77262611d8308daea91b84a0fc71\",\"subChannel\":\"1\",\"device\":\"HUAWEI-HUAWEI MLA-AL10\",\"argsName\":\"userInfoDTO\",\"channel\":\"SAZHJGW\",\"userInfoDTO\":{\"lastUid\":0,\"tabId\":18,\"scene\":0,\"flag\":0,\"operateType\":1},\"version\":\"59600\"}"));
        System.out.print(EncryptUtils.decryptDESString(URLDecoder.decode("SIpXzZyx5kMS6hNSr7axA6COjb3bJTGFGZSuod39x7yEQQPrJkFQ0XtOevrKorcW%2FNHLUdKdimDi%0AJELDdmHdJFCO43bLb6NYhTz8SlIdxrMiwqA3rvroWnFgL871giS2VPu8esbGfNNv0H6B55FaX%2Fzh%0Ar9%2FsmcJHZSwqJfSY%2FCcMicLz%2BKafldXEBfy9v8CgB13mRGUVdB244%2BCfYWr1gMpghEc0h2HSzvcl%0AtRv4cJmWWcyU1Cqe1gL5hJW%2F0WQ9Ht10ZL58PrRGzHQTxabChZHZ6kMOSyhKkY1Q7%2B4DG0zlos1f%0AvSPDeOaso%2FU6gQS7xrIXxdmlRYKGoyoRegK4kk50WM9BoPFs2IQ9%2B3bb2CUw3kr1xNNFJdBPvsdh%0ARmcZg%2BkJ6775op4%3D%0A")).replace("\n", ""));
    }

    private static String DESKey = "spef11kg";
    private static byte[] iv1 = new byte[]{(byte) 18, (byte) 52, (byte) 86, (byte) 120, (byte) -112, (byte) -85, (byte) -51, (byte) -17};

    private EncryptUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static byte[] encryptDES(byte[] bArr) {
        try {
            AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv1);
            Key generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(DESKey.getBytes()));
            Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
            instance.init(1, generateSecret, ivParameterSpec);
            return instance.doFinal(bArr);
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    private static byte[] encryptDESForRequestAndResponse(byte[] bArr) {
        try {
            AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv1);
            DESKey = "yongrun";
            Key generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(DESKey.getBytes()));
            Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
            instance.init(1, generateSecret, ivParameterSpec);
            return instance.doFinal(bArr);
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public static String decryptDESString(String str) {
        byte[] base64Decode = EncryptUtils.base64Decode(str);
        try {
            AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv1);
            Key generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(DESKey.getBytes()));
            Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
            instance.init(2, generateSecret, ivParameterSpec);
            return new String(instance.doFinal(base64Decode), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encryptDESString(String str) {
        return EncryptUtils.base64EncodeString(EncryptUtils.encryptDES(str.getBytes()));
    }

    public static String encryptDESStringForRequestAndResponse(String str) {
        return EncryptUtils.base64EncodeString(EncryptUtils.encryptDESForRequestAndResponse(str.getBytes()));
    }

    private static String base64EncodeString(byte[] bArr) {
        Base64 base64 = new Base64(true);
        return base64.encodeAsString(bArr);
    }

    private static byte[] base64Decode(String str) {
        try {
            Base64 base64 = new Base64(true);
            return base64.decode(str);
        } catch (Exception unused) {
            return new byte[2];
        }
    }


}
