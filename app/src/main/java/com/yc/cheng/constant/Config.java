package com.yc.cheng.constant;

import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;

public class Config {
    public static final boolean DEBUG = false;

    private static String baseUrl = "http://tzc.wk2.com/api/";
    private static String debugBaseUrl = "http://u.wk990.com/api/";

    public static final String INIT_URL = getBaseUrl() + "v1.index/init";
    public static final String REPORT_URL = getBaseUrl() + "v1.index/report";
    public static final String UPDATE_URL = getBaseUrl() + "v1.index/update";


    public static String getBaseUrl() {
        return (DEBUG ? debugBaseUrl : baseUrl);
    }

    public static final String INIT_INFO_KEY = "initInfoKey";
    public static String QRCODE_JUMP_URL = "http://tzc.wk2.com/api/v1.index/index";
    public static final String PATH = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "resources" + File.separator;
}
