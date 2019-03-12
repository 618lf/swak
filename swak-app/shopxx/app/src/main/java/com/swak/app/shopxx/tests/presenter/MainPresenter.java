package com.swak.app.shopxx.tests.presenter;

import com.swak.app.core.base.BasePresenter;
import com.swak.app.core.net.LoadingObserver;
import com.swak.app.core.net.RequestManager;
import com.swak.app.core.net.RetrofitManager;
import com.swak.app.core.net.exception.ResponseException;
import com.swak.app.shopxx.tests.apis.WanAndroidApis;
import com.swak.app.shopxx.tests.bean.BannerBean;
import com.swak.app.shopxx.tests.bean.WanResponse;
import com.swak.app.shopxx.tests.contract.MainContract;

import java.util.List;

import io.reactivex.functions.Function;


public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    @Override
    public void getBannerData() {
        RequestManager.me().execute(this, RetrofitManager.me().create(WanAndroidApis.class).banner().map(new Function<WanResponse<List<BannerBean>>, List<BannerBean>>() {
                    @Override
                    public List<BannerBean> apply(WanResponse<List<BannerBean>> listGankResponse) throws Exception {
                        return null;
                    }
                }),
                new LoadingObserver<List<BannerBean>>(context, true, true) {
                    @Override
                    protected void onSuccess(List<BannerBean> data) {
                        view.onBannerSuccess(data);
                    }

                    @Override
                    protected void onError(ResponseException e) {
                        view.onBannerError(e);
                    }
                });
    }

    @Override
    public void getFriendData() {

    }

    @Override
    public void getZipExecuteData() {

    }

    @Override
    public void getOrderExecuteData() {

    }
}
