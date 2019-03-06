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

package com.swak.app.core.exception;

import com.swak.app.core.ui.Toasts;

/**
 * 异常处理工具类
 *
 * @author 曾繁添
 * @version 1.0
 */
public class ExceptionHandler {

    /**
     * 处理异常
     *
     * @param e
     */
    public static void process(Exception e) {
        if (null != e) {
            e.printStackTrace();
            Toasts.show("程序发生异常，请注意查看错误日志");
        }
    }

    /**
     * 处理异常
     *
     * @param e
     */
    public static void process(Throwable e) {
        if (null != e) {
            e.printStackTrace();
            Toasts.show("程序发生异常，请注意查看错误日志");
        }
    }
}
