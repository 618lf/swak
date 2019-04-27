package com.swak.app.shopxx.tests.presenter;

import com.swak.app.core.base.BasePresenter;
import com.swak.app.core.net.BaseObserver;
import com.swak.app.core.net.RetrofitManager;
import com.swak.app.core.net.exception.ResponseException;
import com.swak.app.shopxx.tests.apis.SplashApis;
import com.swak.app.shopxx.tests.bean.GankResponse;
import com.swak.app.shopxx.tests.bean.SplashBean;
import com.swak.app.shopxx.tests.contract.FuliContract;

import java.util.List;

import io.reactivex.functions.Function;

/**
 * 福利数据获取
 */
public class FuliPresenter extends BasePresenter<FuliContract.View> implements FuliContract.Presenter {

    @Override
    public void refresh() {
        this.executeApi(RetrofitManager.me().create(SplashApis.class).page(1).map(new Function<GankResponse<List<SplashBean>>, List<SplashBean>>() {
            @Override
            public List<SplashBean> apply(GankResponse<List<SplashBean>> listGankResponse) throws Exception {
                return listGankResponse.getResults();
            }
        }), new BaseObserver<List<SplashBean>>(false) {

            @Override
            protected void onSuccess(List<SplashBean> datas) {
                view.onRefreshSuccess(datas);
            }

            @Override
            protected void onError(ResponseException e) {
                view.onPageError(e);
            }
        });
    }

    @Override
    public void loadMore(int page) {
        this.executeApi(RetrofitManager.me().create(SplashApis.class).page(page).map(new Function<GankResponse<List<SplashBean>>, List<SplashBean>>() {
            @Override
            public List<SplashBean> apply(GankResponse<List<SplashBean>> listGankResponse) throws Exception {
                return listGankResponse.getResults();
            }
        }), new BaseObserver<List<SplashBean>>(false) {

            @Override
            protected void onSuccess(List<SplashBean> datas) {
                view.onPageSuccess(datas);
            }

            @Override
            protected void onError(ResponseException e) {
                view.onPageError(e);
            }
        });
    }
}
