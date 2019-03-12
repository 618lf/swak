package com.swak.app.core.base;

import android.os.Bundle;

import java.lang.reflect.ParameterizedType;

/**
 * 基础的 mvp 实现
 *
 * @param <P>
 */
public abstract class BaseActivity<P extends BasePresenter> extends ActivityBase implements BaseView {
    protected P presenter;

    /**
     * 定义初始化的顺序
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initPresenter();
        this.initView(savedInstanceState);
    }

    /**
     * 优先初始化 presenter
     *
     * @param savedInstanceState
     */
    @Override
    public void _initView(Bundle savedInstanceState) {
    }

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    public void initPresenter() {
        presenter = this.newPresenter();
        if (presenter != null) {
            presenter.context = this;
            presenter.setVM(this);
        }
    }

    /**
     * 创建默认的 Presenter
     *
     * @param <T>
     * @return
     */
    protected <T> T newPresenter() {
        try {
            return ((Class<T>) ((ParameterizedType) (this.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[0])
                    .newInstance();
        } catch (InstantiationException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (ClassCastException ignored) {
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detach();
        }
    }
}
