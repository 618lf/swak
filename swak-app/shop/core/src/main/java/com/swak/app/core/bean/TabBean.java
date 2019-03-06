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

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Fragment类型的Tab数据模型
 * 
 * @author 曾繁添
 * @version 1.0
 *
 */
public class TabBean extends BaseBean {
  
  private static final long serialVersionUID = -814914448481197650L;

  /**
   * tab图标
   */
  public String icon = "";

  /**
   * tab显示文本
   */
  public String label = "默认";

  /**
   * tab对应的id
   */
  public String value = "999";

  /**
   * 对应Fragment
   */
  public Class<? extends Fragment> clss;

  /**
   * Fragment对应参数
   */
  public Bundle args;

  /**
   * 额外扩展参数
   */
  public Object extParam;

  public TabBean(String icon, String label) {
    this.icon = icon;
    this.label = label;
  }

  public TabBean(String label, Class<? extends Fragment> clss) {
    this(label,clss,null);
  }

  public TabBean(String label, Class<? extends Fragment> clss, Bundle args) {
    this(label,label,clss,args);
  }

  public TabBean(String label, Class<? extends Fragment> clss, Bundle args, Object extParam) {
    this(label,label,clss,args,extParam);
  }

  public TabBean(String label, String value, Class<? extends Fragment> clss, Bundle args) {
    this(label,value,clss,args,null);
  }

  public TabBean(String label, String value, Class<? extends Fragment> clss, Bundle args, Object extParam) {
    this.label = label;
    this.value = value;
    this.clss = clss;
    this.args = args;
    this.extParam = extParam;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Class<?> getClss() {
    return clss;
  }

  public void setClss(Class<? extends Fragment> clss) {
    this.clss = clss;
  }

  public Bundle getArgs() {
    return args;
  }

  public void setArgs(Bundle args) {
    this.args = args;
  }
}
