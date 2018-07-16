package com.swak.storage;

import java.io.InputStream;

/**
 * 存储服务
 * 
 * @author root
 */
public interface Storager {

	/**
	 * 上传文件 fileName --- 包含存储的路径
	 * 
	 * @param datas
	 * @param fileName
	 * @return
	 */
	public String upload(byte[] datas, String group, String fileName);

	/**
	 * 删除文件
	 * 
	 * @param group
	 * @param fileName
	 * @return
	 */
	public int delete(String group, String fileName);

	/**
	 * 修改文件
	 * 
	 * @param datas
	 * @param fileName
	 * @param oldFileName
	 * @return
	 */
	public String modify(byte[] datas, String group, String fileName, String oldFileName);

	/**
	 * 下载文件
	 * 
	 * @param fileName
	 * @return
	 */
	public InputStream download(String fileName);

	/**
	 * 文件服务器显示的地址
	 * 
	 * @return
	 */
	public String getShowUrl(String url);
}