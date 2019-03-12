package com.swak.app.core.base;

import android.os.Bundle;

import java.lang.reflect.ParameterizedType;

/**
 * java类作用描述
 *
 * @Author: 李锋
 * @Date: 2019/3/9 16:43
 * @Version: 1.0
 */
public abstract class BaseFragment<P extends BasePresenter> extends FragmentBase implements BaseView {

    protected P presenter;

    /**
     * 定义初始化的顺序
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    protected void _initView(Bundle savedInstanceState) {
    }

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    public void initPresenter() {
        presenter = this.newPresenter();
        if (presenter != null) {
            presenter.context = this.getActivity();
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
        } catch (java.lang.InstantiationException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (ClassCastException ignored) {
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detach();
        }
    }
}