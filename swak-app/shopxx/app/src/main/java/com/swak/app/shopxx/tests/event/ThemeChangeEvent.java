package com.swak.app.shopxx.tests.event;

import java.io.Serializable;

/**
 * 定义主题切换的事件
 */
public class ThemeChangeEvent implements Serializable {
    private String theme;

    public ThemeChangeEvent(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
