package com.swak.app.shopxx.tests.presenter;

import com.swak.app.core.base.BasePresenter;
import com.swak.app.core.net.BaseObserver;
import com.swak.app.core.net.RetrofitManager;
import com.swak.app.core.net.exception.ResponseException;
import com.swak.app.shopxx.tests.apis.SplashApis;
import com.swak.app.shopxx.tests.bean.GankResponse;
import com.swak.app.shopxx.tests.bean.SplashBean;
import com.swak.app.shopxx.tests.contract.SplashContract;

import java.util.List;

import io.reactivex.functions.Function;

/**
 * 首页的相关服务
 */
public class SplashPresenter extends BasePresenter<SplashContract.View> implements SplashContract.Presenter {

    @Override
    public void getSplashPic() {
        this.executeApi(RetrofitManager.me().create(SplashApis.class).getSplashPic().map(new Function<GankResponse<List<SplashBean>>, List<SplashBean>>() {
            @Override
            public List<SplashBean> apply(GankResponse<List<SplashBean>> listGankResponse) throws Exception {
                return listGankResponse.getResults();
            }
        }), new BaseObserver<List<SplashBean>>(false) {

            @Override
            protected void onSuccess(List<SplashBean> datas) {
                view.onSplashSuccess(datas);
            }

            @Override
            protected void onError(ResponseException e) {
                view.onSplashError(e);
            }
        });
    }
}
