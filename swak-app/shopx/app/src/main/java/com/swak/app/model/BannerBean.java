package com.swak.app.model;

import java.io.Serializable;

/**
 * 作者：kkan on 2018/02/26
 * 当前类注释:
 * 首页轮播
 */

public class BannerBean implements Serializable {
    private String banner_url;
    private String tips;

    public BannerBean() {
    }
    public BannerBean(String banner_url) {
        this.banner_url = banner_url;
    }

    public String getBanner_url() {
        return banner_url==null?"":banner_url;
    }

    public void setBanner_url(String banner_url) {
        this.banner_url = banner_url;
    }

    public String getTips() {
        return tips==null?"":tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
