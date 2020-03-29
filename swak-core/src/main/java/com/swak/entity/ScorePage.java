package com.swak.entity;

import java.io.Serializable;

/**
 * 基于评分的分页对象
 *
 * @author: lifeng
 * @date: 2020/3/29 11:21
 */
public class ScorePage extends Page implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 在这个之后取数据不包括这个
     */
    private String after;

    public ScorePage() {
    }

    public <T> ScorePage(ScorePage page) {
        super(page.getParam(), page.getData());
        this.setAfter(page.getAfter());
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}