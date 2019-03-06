package com.swak.app.core;

/**
 * 基础的常量
 */
public interface IConstant {

    /**
     * 用于 intent 传输动画类型数据
     */
    String ANIMATION_TYPE = "AnimationType";
    /**
     * 无动画
     */
    int NONE = 0;
    /**
     * 左右动画
     */
    int LEFT_RIGHT = 1;
    /**
     * 上下动画
     */
    int TOP_BOTTOM = 2;
    /**
     * 淡入淡出
     */
    int FADE_IN_OUT = 3;

    /*** 资源类型-array **/
    String ARRAY = "array";

    /*** 资源类型-attr **/
    String ATTR = "attr";

    /*** 资源类型-anim **/
    String ANIM = "anim";

    /*** 资源类型-bool **/
    String BOOL = "bool";

    /*** 资源类型-color **/
    String COLOR = "color";

    /*** 资源类型-dimen **/
    String DIMEN = "dimen";

    /*** 资源类型-drawable **/
    String DRAWABLE = "drawable";

    /*** 资源类型-id **/
    String ID = "id";

    /*** 资源类型-id **/
    String INTEGER = "integer";

    /*** 资源类型-layout **/
    String LAYOUT = "layout";

    /*** 资源类型-drawable **/
    String STRING = "string";

    /*** 资源类型-style **/
    String STYLE = "style";

    /*** 资源类型-styleable **/
    String STYLEABLE = "styleable";

    /**
     * 查看器图片默认选中位置
     */
    String PICTURE_VIEWER_DEFAULT_POSTION = "defaultPostion";

    /**
     * 查看器数据源
     */
    String PICTURE_VIEWER_DATASOURCE = "pictureViewerDatasource";

    /**
     * 图片裁剪A
     */
    String ACTION_CROP = "com.android.camera.action.CROP";

    /**
     * [相册选择]选择请求码
     */
    int ALBUM_REQUEST_CODE = 128;

    /**
     * [立即拍照]选择请求码
     */
    int CAMERA_REQUEST_CODE = 127;

    /**
     * [裁剪图片]选择请求码
     */
    int CROPER_REQUEST_CODE = 126;

    /**
     * Fragment依附Activity共享数据key
     */
    String HOST_SHARE_DATA = "hostSharedData";

    /**
     * 分享平台-微信朋友圈
     */
    String PLATFORM_WECHAT_MOMENTS = "0";

    /**
     * 分享平台-微信好友
     */
    String PLATFORM_WECHAT_FRIENDS = "1";

    /**
     * 分享平台-新浪微博
     */
    String PLATFORM_SINAWEIBO = "2";

    /**
     * 分享平台-短信
     */
    String PLATFORM_SHORTMESSAGE = "3";

    /**
     * 分享平台-QQ好友
     */
    String PLATFORM_QQ_FRIENDS = "4";

    /**
     * 分享平台- QQ空间
     */
    String PLATFORM_QZONE = "5";

    /**
     * 默认ViewTemplet与UI(Activity/Fragment交互桥接)
     */
    String DEFAULT_VIEW_TEMPLET_CONN = "default_view_templet_conn";

    /**
     * 颜色常量
     */
    interface IColor{

        /**
         * 橙色-#FF801a
         */
        String COLOR_FF801A = "#FF801a";

        /**
         * 蓝色-#508CEE
         */
        String COLOR_508CEE = "#508CEE";

        /**
         * 白色-#FFFFFF
         */
        String COLOR_FFFFFF = "#FFFFFF";

        /**
         * 黑色-#efeff4
         */
        String COLOR_EFEFF4 = "#efeff4";

        /**
         * 透明色
         */
        String COLOR_TRANSPARENT = "#00000000";

        /**
         * 黑色-#f9f9f9
         */
        String COLOR_F9F9F9 = "#f9f9f9";

        /**
         * 黑色-#F5F5F5
         */
        String COLOR_F5F5F5 = "#F5F5F5";

        /**
         * 黑色-#F0F0F0
         */
        String COLOR_F0F0F0 = "#F0F0F0";

        /**
         * 黑色-#33333
         */
        String COLOR_333333 = "#333333";

        /**
         * 黑色-#444444
         */
        String COLOR_444444 = "#444444";

        /**
         * 黑色-#666666
         */
        String COLOR_666666 = "#666666";

        /**
         * 黑色-#999999
         */
        String COLOR_999999 = "#999999";
    }
}
