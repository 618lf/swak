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

package com.swak.app.core.model;

import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * 发送Notification通知实体
 * 
 * @author 曾繁添
 * @version 1.0
 * 
 */
public class MessageNotification extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 681166507845221063L;

	/**
	 * 状态栏提示信息图标（必须）
	 */
	private int iconResId;

	/**
	 * 状态栏提示信息文本（必须）
	 */
	private String statusBarText;

	/**
	 * 消息标题
	 */
	private String msgTitle;

	/**
	 * 消息内容
	 */
	private String msgContent;

	/**
	 * 点击消息跳转的界面
	 */
	private Class forwardComponent;
	
	/**
	 * 点击消息跳转界面需携带的数据
	 */
	private Bundle extras;

	/**
	 * 自定义消息通知布局View
	 */
	private RemoteViews mRemoteViews;

	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	public String getStatusBarText() {
		return statusBarText;
	}

	public void setStatusBarText(String statusBarText) {
		this.statusBarText = statusBarText;
	}

	public String getMsgTitle() {
		return msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public Class getForwardComponent() {
		return forwardComponent;
	}

	public void setForwardComponent(Class forwardComponent) {
		this.forwardComponent = forwardComponent;
	}

	public Bundle getExtras() {
		return extras;
	}

	public void setExtras(Bundle extras) {
		this.extras = extras;
	}

	public RemoteViews getmRemoteViews() {
		return mRemoteViews;
	}

	public void setmRemoteViews(RemoteViews mRemoteViews) {
		this.mRemoteViews = mRemoteViews;
	}
}
