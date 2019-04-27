package com.swak.app.shopxx.tests.contract;

import com.swak.app.core.base.BaseView;
import com.swak.app.core.net.exception.ResponseException;
import com.swak.app.shopxx.tests.bean.SplashBean;

import java.util.List;

/**
 * 福利
 */
public interface FuliContract {

    interface View extends BaseView {

        /**
         * 获取列表成功
         *
         * @param datas
         */
        void onRefreshSuccess(List<SplashBean> datas);

        /**
         * 获取列表成功
         *
         * @param datas
         */
        void onPageSuccess(List<SplashBean> datas);

        /**
         * 获取列表失败
         *
         * @param e
         */
        void onPageError(ResponseException e);
    }

    interface Presenter {

        /**
         * 刷新数据
         */
        void refresh();

        /**
         * 加载更多
         */
        void loadMore(int page);
    }
}

