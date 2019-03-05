package com.swak.app.api;


import com.swak.app.model.PersonalBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 作者：kkan on 2017/11/30
 * 当前类注释:
 * 请求地址
 * 表单的方式传递键值对@FormUrlEncoded  不能和@Body 同时使用
 * 单文件上传@Multipart  例 @Part MultipartBody.Part photo 不能和@Body 同时使用
 * 多文件上传@PartMap 例 registerUser(@PartMap Map<String, RequestBody> params,  @Part("password") RequestBody password);
 * <p>
 * 使用json 方式请求
 * 更改 Content-Type   @Headers({"Content-Type: application/json","Accept: application/json"})
 * 参数设置为registerUser(@PartMap Map<String, RequestBody> params,  @Part("password") RequestBody password);
 *
 * 表单方式请求
 * 更改@FormUrlEncoded
 * 更改 @POST("system/getLastVersion")
 * 更改 getLastVersion(@FieldMap Map<String, String> map);
 */
public interface HttpUrlService {
    //版本更新
    @FormUrlEncoded
    @POST("system/getLastVersion")
    Observable<HttpRespose<List<PersonalBean>>> getLastVersion(@FieldMap Map<String, String> map);
}
