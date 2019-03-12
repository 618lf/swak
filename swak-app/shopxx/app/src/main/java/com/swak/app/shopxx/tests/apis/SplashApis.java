package com.swak.app.shopxx.tests.apis;

import com.swak.app.shopxx.tests.Url;
import com.swak.app.shopxx.tests.bean.GankResponse;
import com.swak.app.shopxx.tests.bean.SplashBean;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * 首页的Api 服务
 */
public interface SplashApis {

    String BASE_URL = Url.GANK_API;

    @GET("data/%e7%a6%8f%e5%88%a9/3/1")
    Observable<GankResponse<List<SplashBean>>> getSplashPic();
}
