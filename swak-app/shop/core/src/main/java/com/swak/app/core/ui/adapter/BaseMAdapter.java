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

package com.swak.app.core.ui.adapter;

import android.app.Activity;
import android.content.Context;

import com.swak.app.core.IConstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Adapter基类
 * @author 曾繁添
 * @version 1.0
 */
public abstract class BaseMAdapter extends android.widget.BaseAdapter implements IConstant {

	/** 数据存储集合 **/
	private List<IAdapterModel> mDataList = new ArrayList<>();
	/** Context上下文 **/
	private Context mContext;
	/** 每一页显示条数 **/
	private int mPerPageSize = 10;
	/**日志输出标志**/
	protected final String TAG = this.getClass().getSimpleName();

	public BaseMAdapter(Context mContext) {
		this.mContext = mContext;
		this.mPerPageSize = 10;
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public IAdapterModel getItem(int position) {
		if (position < mDataList.size())
			return mDataList.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * 获取当前页
	 * @return 当前页
	 */
	public int getPageNo(){
		return (getCount() / mPerPageSize) + 1;
	}
	
	   /**
     * 获取分页的偏移量(供列表带删除功能时使用)
     * @return
     */
    public int getOffsetCount(){
        return (getCount() - 1) + 1;
    }
	
    /**
     * 获取当前ListView绑定的数据源
     * @return
     */
    public List<IAdapterModel> gainDataSource(){
        return mDataList;
    }
    
    /**
     * 获取每页显示的数量
     * @return
     */
    public int getPerPagerSize(){
        return mPerPageSize;
    }
    
    /**
     * 设置每一页显示条目数
     * @param pageSize
     */
    public void setPerPageSize(int pageSize){
        this.mPerPageSize = pageSize;
    }
    
	/**
	 * 添加数据
	 * @param object 数据模型
	 */
	public boolean addItem(IAdapterModel object){
		return mDataList.add(object);
	}
	
	/**
	 * 在指定索引位置添加数据
	 * @param location 索引
	 * @param object 数据模型
	 */
	public void addItem(int location,IAdapterModel object){
	     mDataList.add(location, object);
	}
	
	/**
	 * 集合方式添加数据
	 * @param collection 集合
	 */
	public boolean addItem(Collection< ? extends IAdapterModel> collection){
		return mDataList.addAll(collection);
	}
	
	/**
	 * 在指定索引位置添加数据集合
	 * @param location 索引
	 * @param collection 数据集合
	 */
	public boolean addItem(int location, Collection< ? extends IAdapterModel> collection){
		return mDataList.addAll(location,collection);
	}
	
	/**
	 * 移除指定对象数据
	 * @param object 移除对象
	 * @return 是否移除成功
	 */
	public boolean removeItem(IAdapterModel object){
		return mDataList.remove(object);
	}
	
	/**
	 * 移除指定索引位置对象
	 * @param location 删除对象索引位置
	 * @return 被删除的对象
	 */
	public Object removeItem(int location){
	    return mDataList.remove(location);
	}
	
	/**
	 * 移除指定集合对象
	 * @param collection 待移除的集合
	 * @return 是否移除成功
	 */
	public boolean removeAll(Collection< ? extends IAdapterModel> collection){
		return mDataList.removeAll(collection);
	}
	
	/**
	 * 清空数据
	 */
	public void clear() {
		mDataList.clear();
	}

	/**
	 * 重新new集合对象
	 */
	public void newAnList() {
		mDataList = new ArrayList<>();
	}

	/**
	 * 获取Activity方法
	 * @return Activity的子类
	 */
	public Activity getActivity(){
		if(mContext instanceof Activity){
			return (Activity) mContext;
		}
		return null;
	}

	/**
	 * 获取Context
	 * @return
     */
	public Context getContext(){
		return mContext;
	}
}
