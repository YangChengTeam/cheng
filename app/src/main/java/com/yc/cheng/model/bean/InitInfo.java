package com.yc.cheng.model.bean;

import java.util.List;

public class InitInfo {

    private int type;
    private List<String> pics;
    private String video;
    private List<String> voices;
    private List<String> bgs;
    private String qrcode_jump_url;
    private String qrcode_logo_url;

    public String getQrcode_logo_url() {
        return qrcode_logo_url;
    }

    public void setQrcode_logo_url(String qrcode_logo_url) {
        this.qrcode_logo_url = qrcode_logo_url;
    }

    private float volum_value;
    private float brightness_value;
    private String update_time;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public List<String> getVoices() {
        return voices;
    }

    public void setVoices(List<String> voices) {
        this.voices = voices;
    }

    public List<String> getBgs() {
        return bgs;
    }

    public void setBgs(List<String> bgs) {
        this.bgs = bgs;
    }

    public String getQrcode_jump_url() {
        return qrcode_jump_url;
    }

    public void setQrcode_jump_url(String qrcode_jump_url) {
        this.qrcode_jump_url = qrcode_jump_url;
    }

    public float getVolum_value() {
        return volum_value;
    }

    public void setVolum_value(float volum_value) {
        this.volum_value = volum_value;
    }

    public float getBrightness_value() {
        return brightness_value;
    }

    public void setBrightness_value(float brightness_value) {
        this.brightness_value = brightness_value;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

}
