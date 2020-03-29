package com.swak.entity;

/**
 * 数据类型
 *
 * @author: lifeng
 * @date: 2020/3/29 11:12
 */
public enum DataType {
    /**
     * 文本
     */
    STRING("文本"),

    /**
     * 数字
     */
    NUMBER("数字"),
    /**
     * 日期
     */
    DATE("日期"),

    /**
     * 布尔
     */
    BOOLEAN("布尔"),

    /**
     * 金额
     */
    MONEY("金额");
    private String name;

    DataType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}