package com.swak.fx.config;

import java.io.File;
import java.util.Comparator;

/**
 * 备份
 * 
 * @author lifeng
 */
public class Backup {

	private String name;
	private File file;
	private File save;

	public File getSave() {
		return save;
	}
	public void setSave(File save) {
		this.save = save;
	}
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

	// 显示的时候从高往低版本显示
	public static Comparator<Backup> show = new Comparator<Backup>() {
		@Override
		public int compare(Backup o1, Backup o2) {
			return o2.name.compareTo(o1.name);
		}
	};
}
