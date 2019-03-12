package com.swak.app.shopxx.tests.contract;

import com.swak.app.core.base.BaseView;
import com.swak.app.core.net.exception.ResponseException;
import com.swak.app.shopxx.tests.bean.BannerBean;
import com.swak.app.shopxx.tests.bean.FriendBean;

import java.util.List;

public interface MainContract {
    interface View extends BaseView {
        void onBannerSuccess(List<BannerBean> data);

        void onBannerError(ResponseException e);

        void onFriendSuccess(List<FriendBean> data);

        void onFriendError(ResponseException e);

        void onZipExecuteSuccess(String data);

        void onOrderExecuteSuccess(List<FriendBean> data);
    }

    interface Presenter {
        void getBannerData();

        void getFriendData();

        void getZipExecuteData();

        void getOrderExecuteData();
    }
}
