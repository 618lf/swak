package com.veni.tools.base;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 基类view
 */
public interface BaseView {
    /*******内嵌加载*******/
//    void showLoading(String loadtipmsg);
//    void stopLoading();
//    void showErrorTip(String errtipmsg);
//    void onError(int code,String errtipmsg);
    void onErrorSuccess(int code, String message, boolean isSuccess, boolean showTips);
}
