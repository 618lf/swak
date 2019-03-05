package com.swak.app.api;

import java.io.Serializable;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 *      封装服务器返回数据
 */
public class HttpRespose<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public boolean success() {
        return code==1001;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpRespose{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
