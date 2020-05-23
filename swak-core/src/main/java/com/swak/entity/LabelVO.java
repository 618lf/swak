package com.swak.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 不管对不对，请统一这样使用
 * <p>
 * label value --- 名称定义的有问题，很容易让人误解
 * 特别作为select的option时
 * 特统一下：
 * label --- 对应option 的label （看得见的那个值）
 * value --- 对应option 的value （看不见的那个值（会提交到后台））
 *
 * @author lifeng
 */
@Data
public class LabelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String label;
    private String value;
    private Integer count;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public LabelVO() {
    }

    public LabelVO(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public LabelVO(String label, String value, Integer count) {
        this.label = label;
        this.value = value;
        this.count = count;
    }

    public static LabelVO newLabel(String label, String value) {
        return new LabelVO(label, value);
    }

    public static LabelVO newLabel(String label, String value, Integer count) {
        return new LabelVO(label, value, count);
    }
}