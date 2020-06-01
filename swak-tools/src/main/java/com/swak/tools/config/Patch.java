package com.swak.tools.config;

import java.io.File;
import java.util.Comparator;

/**
 * 补丁
 * 
 * @author lifeng
 */
public class Patch {
	private File file;
	private String name;
	private String remarks;
	private String version;

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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	// 版本
	private static void version(Patch patch) {
		String name = patch.getName();
		if (name != null) {
			String[] names = name.split(":");
			if (names != null && names.length == 3) {
				patch.version = names[1];
			}
		}
	}

	/**
	 * 创建补丁对象
	 * 
	 * @param name
	 * @param remarks
	 * @param createDate
	 * @return
	 */
	public static Patch newPatch(File file) {
		Patch patch = new Patch();
		patch.setFile(file);
		patch.setName(file.getName());
		version(patch);
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
	public static Patch newPatch(File file, String name) {
		Patch patch = new Patch();
		patch.setFile(file);
		patch.setName(name);
		version(patch);
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
	public static Patch newPatch(String name, String remarks) {
		Patch patch = new Patch();
		patch.setName(name);
		patch.setRemarks(remarks);
		version(patch);
		return patch;
	}

	// 显示的时候从高往低版本显示
	public static Comparator<Patch> show = new Comparator<Patch>() {
		@Override
		public int compare(Patch o1, Patch o2) {
			if (o1.version == null) {
				return 1;
			}
			if (o2.version == null) {
				return -1;
			}
			return o2.version.compareTo(o1.version);
		}
	};
	// 安装的时候从低往高版本显示
	public static Comparator<Patch> install = new Comparator<Patch>() {
		@Override
		public int compare(Patch o1, Patch o2) {
			if (o1.version == null) {
				return 1;
			}
			if (o2.version == null) {
				return -1;
			}
			return o1.version.compareTo(o2.version);
		}
	};
}
