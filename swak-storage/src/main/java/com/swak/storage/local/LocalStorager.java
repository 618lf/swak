package com.swak.storage.local;

import java.io.File;
import java.io.InputStream;

import com.swak.storage.Storager;
import com.swak.utils.FileUtils;
import com.swak.utils.StringUtils;

/**
 * 本地的存储服务
 * 
 * @author root
 */
public class LocalStorager implements Storager {

	private String storagePath; // 存储的根目录
	private String urlPath;// 访问的前缀
	private String domain;// 域名

	/**
	 * 上传一个文件
	 */
	@Override
	public String upload(byte[] datas, String group, String fileName) {
		File file = new File(storagePath, fileName);
		File parentPath = file.getParentFile();
		if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
			return null;
		}
		if (!parentPath.canWrite()) {
			return null;
		}
		FileUtils.write(file, datas);
		return new StringBuilder(urlPath).append(StringUtils.remove(file.getAbsolutePath(), storagePath)).toString();
	}

	/**
	 * 删除当前文件
	 */
	@Override
	public int delete(String group, String fileName) {
		String realFileName = fileName;
		if (StringUtils.isNotBlank(realFileName)) {
			realFileName = StringUtils.remove(realFileName, urlPath);
			realFileName = new StringBuilder(storagePath).append(realFileName).toString();
		}
		File file = new File(realFileName);
		if (file.exists() && file.isFile()) {
			file.delete();
		} else {
			return 0;
		}
		return 1;
	}

	/**
	 * 修改-删除、上传
	 */
	@Override
	public String modify(byte[] datas, String group, String fileName, String oldFileName) {
		this.delete(group, oldFileName);
		return this.upload(datas, group, oldFileName);
	}

	/**
	 * 下载服务
	 */
	@Override
	public InputStream download(String fileName) {
		String realFileName = fileName;
		if (StringUtils.isNotBlank(realFileName)) {
			realFileName = StringUtils.remove(realFileName, urlPath);
			realFileName = new StringBuilder(storagePath).append(realFileName).toString();
		}
		File file = new File(realFileName);
		if (file != null && file.exists()) {
			return FileUtils.open(file);
		}
		return null;
	}

	/**
	 * 得到可以显示的地址
	 */
	@Override
	public String getShowUrl(String url) {
		return new StringBuilder(domain).append(url).toString();
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}