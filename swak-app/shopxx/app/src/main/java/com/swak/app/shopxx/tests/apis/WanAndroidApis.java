package com.swak.app.shopxx.tests.apis;

import com.swak.app.shopxx.tests.Url;
import com.swak.app.shopxx.tests.bean.BannerBean;
import com.swak.app.shopxx.tests.bean.FriendBean;
import com.swak.app.shopxx.tests.bean.WanResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Wan Android 的 api
 */
public interface WanAndroidApis {

    String BASE_URL = Url.WAN_ANDROID_API;

    @GET("banner/json")
    Observable<WanResponse<List<BannerBean>>> banner();

    @GET("friend/json")
    Observable<WanResponse<List<FriendBean>>> friend();
}
