package com.tmt.manage.widgets.theme.upgrade;

import java.io.File;

/**
 * 备份
 * 
 * @author lifeng
 */
public class Backup {

	private String name;
	private File file;
	private String size;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * 创建补丁对象
	 * 
	 * @param name
	 * @param remarks
	 * @param createDate
	 * @return
	 */
	public static Backup newBackup(String name, String size) {
		Backup patch = new Backup();
		patch.setName(name);
		patch.setSize(size);
		return patch;
	}

	/**
	 * 创建补丁对象
	 * 
	 * @param name
	 * @param remarks
	 * @param createDate
	 * @return
	 */
	public static Backup newBackup(File file) {
		Backup patch = new Backup();
		patch.setFile(file);
		patch.setName(file.getName());
		return patch;
	}
}
