package com.tmt.manage.widgets.theme.upgrade;

/**
 * 备份
 * 
 * @author lifeng
 */
public class Backup {

	private String name;
	private String createDate;
	private String size;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
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
	public static Backup newBackup(String name, String createDate, String size) {
		Backup patch = new Backup();
		patch.setName(name);
		patch.setCreateDate(createDate);
		patch.setSize(size);
		return patch;
	}
}
