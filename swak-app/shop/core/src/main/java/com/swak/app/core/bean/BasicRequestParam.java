/*
 *     Android基础开发个人积累、沉淀、封装、整理共通
 *     Copyright (c) 2016. 曾繁添 <zftlive@163.com>
 *     Github：https://github.com/zengfantian || http://git.oschina.net/zftlive
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.swak.app.core.bean;

/**
 * 请求基本参数
 *
 * @author 曾繁添
 * @version 1.0
 *
 */
public class BasicRequestParam extends BaseBean {

    private static final long serialVersionUID = 8375360327318599130L;

    /**
     * 设备ID
     */
    public String deveceId = "00000000";

    /**
     * 客户端版本号
     */
    public String appVersionName = "1.0";

    /**
     * 客户端版本号编码
     */
    public String appVersionCode = "1";

    /**
     * 客户端平台：Android/IOS/WP
     */
    public String platform = "Android";

    /**
     * 渠道号
     */
    public String channelId = "";

    /**
     * 服务器接口版本号
     */
    public int interfaceVersion = 100;

    /**
     * 网络类型：2G/3G/4G/WIFI
     */
    public String networkType = "";

    /**
     * 系统版本
     */
    public String osVersion = "";

    /**
     * 设备型号
     */
    public String deviceModel = "";

    /**
     * 屏幕分辨率-宽度
     */
    public String screenWidth = "";

    /**
     * 屏幕分辨率-高度
     */
    public String screenHeight = "";

    /**
     * 密度
     */
    public float desnity = 1.0f;

}
