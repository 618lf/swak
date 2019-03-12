package com.swak.app.shopxx.tests.contract;

import com.swak.app.core.base.BaseView;
import com.swak.app.core.net.exception.ResponseException;
import com.swak.app.shopxx.tests.bean.SplashBean;

import java.util.List;

/**
 * 启动界面
 */
public interface SplashContract {
    interface View extends BaseView {

        /**
         * 获取图片成功
         * @param datas
         */
        void onSplashSuccess(List<SplashBean> datas);

        /**
         * 获取图片失败
         * @param e
         */
        void onSplashError(ResponseException e);
    }

    interface Presenter {

        /**
         * 获取首页图片
         */
        void getSplashPic();
    }
}