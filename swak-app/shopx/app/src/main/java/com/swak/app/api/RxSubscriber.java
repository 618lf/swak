package com.swak.app.api;

import android.content.Context;
import android.os.NetworkOnMainThreadException;

import com.veni.tools.JsonTools;
import com.veni.tools.LogTools;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 订阅封装
 * <p>
 * ----------------使用例子----------------*
 * <p>
 * HttpManager.getOkHttpUrlService() .compose(RxSchedulers.<HttpRespose<Bean>io_main())
 * .subscribe(new RxSubscriber<Bean>(mContext, "加载框信息,不传不显示") {
 * public void _onNext(Bean data) {
 * //处理返回数据,根据需要返回给页面
 * }
 * public void _onError(int code, String message, boolean issuccess) {
 * //处理异常数据
 *  mView.onErrorSuccess(code, message, issuccess,false);
 * }
 * });
 */
public abstract class RxSubscriber<T> implements Observer<HttpRespose<T>> {
    private final int RESPONSE_FATAL_EOR = -1;//返回数据失败,严重的错误
    private int errorCode = -1111;//错误码
    private String errorMsg = "未知的错误！";//错误信息
    private Disposable disposable;

    /**
     * 构造方法
     * 无加载弹窗
     * 显示返回提示信息
     */
    public RxSubscriber() {
    }

    /**
     * 构造方法
     * 不显示返回提示信息
     * 有加载弹窗
     *@param loadmsg loadmsg可为空
     * 具体在RxHttpTipLoadDialog设置
     */
    public RxSubscriber(Context context, String loadmsg) {
        RxHttpTipLoadDialog.getHttpTipLoadDialog().showDialog(context, loadmsg);
    }

    /*抽象方法*/
    public abstract void _onNext(T t);

    /*抽象方法
     * 需要在 onError中同意处理异常 则可以写成
     * @CallSuper
     * public void _onError(int code, String message){
     * //要统一处理的异常
     *  }
     */
    public abstract void onErrorSuccess(int code, String message, boolean issuccess);
//    public abstract void _onError(int code, String message);

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    /**
     * 请求数据处理
     */
    @Override
    public void onNext(HttpRespose<T> response) {
        disposeIt();
        LogTools.e("Observer.onNext", JsonTools.toJson(response));
        //服务器返回对应的code码
        if (response.success()) {
            // 这里拦截一下使用测试
            _onNext(response.getData());
        }
//        else {
//            _onError(response.getCode(), response.getMsg());
//        }

        onErrorSuccess(response.getCode(), response.getMsg(),response.success());
    }

    /**
     * 异常处理
     */
    @Override
    public void onError(Throwable t) {
        LogTools.e("Observer.java", t.getMessage() + "");
        disposeIt();
        if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            errorCode = httpException.code();
            errorMsg = httpException.getMessage();
            getErrorMsg(httpException);
        } else if (t instanceof SocketTimeoutException) {  //VPN open
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "服务器响应超时";
        } else if (t instanceof ConnectException) {
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "网络连接异常，请检查网络";
        } else if (t instanceof UnknownHostException) {
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "无法解析主机，请检查网络连接";
        } else if (t instanceof UnknownServiceException) {
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "未知的服务器错误";
        } else if (t instanceof IOException) {   //飞行模式等
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "没有网络，请检查网络连接";
        } else if (t instanceof NetworkOnMainThreadException) {
            //主线程不能网络请求，这个很容易发现
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "主线程不能网络请求";
            // ... ...
        } else if (t instanceof RuntimeException) {
            //很多的错误都是extends RuntimeException
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "运行时错误" + t.toString();
        }
        onErrorSuccess(errorCode, errorMsg,false);
    }

    @Override
    public void onComplete() {
        disposeIt();
    }

    /**
     * 销毁disposable
     */
    private void disposeIt() {
        RxHttpTipLoadDialog.getHttpTipLoadDialog().dismissDialog();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    /**
     * 获取详细的错误的信息 errorCode,errorMsg 尝试UniCode转码
     * <p>
     * 以登录的时候的Grant_type 故意写错为例子,这个时候的http 应该是直接的返回401=httpException.code()
     * 但是是怎么导致401的？我们的服务器会在respose.errorBody 中的content 中说明
     */
    private final void getErrorMsg(HttpException httpException) {
        String errorBodyStr = "";
        try {      //我们的项目需要的UniCode转码 ,!!!!!!!!!!!!!!
            errorBodyStr = TextConvertUtils.convertUnicode(httpException.response().errorBody().string());
        } catch (IOException ioe) {
            LogTools.e("errorBodyStr ioe:", ioe.toString());
        }
        try {
            HttpRespose errorResponse = JsonTools.parseObject(errorBodyStr, HttpRespose.class);
            if (null != errorResponse) {
                errorCode = errorResponse.getCode();
                errorMsg = errorResponse.getMsg();
            }
        } catch (Exception jsonException) {
            jsonException.printStackTrace();
        }
    }


}
