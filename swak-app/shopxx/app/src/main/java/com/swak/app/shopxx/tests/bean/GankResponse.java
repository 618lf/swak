package com.swak.app.shopxx.tests.bean;

import java.io.Serializable;

/**
 * 干货集中营 Api 基类
 */
public class GankResponse<T> implements Serializable {

    private boolean error;
    private T results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}
