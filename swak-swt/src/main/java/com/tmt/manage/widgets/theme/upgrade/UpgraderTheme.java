package com.tmt.manage.widgets.theme.upgrade;

import java.util.List;

import com.tmt.manage.widgets.theme.Theme;

/**
 * 升级模式
 * 
 * @author lifeng
 */
public abstract class UpgraderTheme implements Theme {

	@Override
	public String name() {
		return "升级";
	}

	@Override
	public String path() {
		return "upgrade.UpgraderApp";
	}

	/**
	 * background
	 * 
	 * @return
	 */
	public abstract Action background();

	/**
	 * logo
	 * 
	 * @return
	 */
	public abstract Action logo();

	/**
	 * close
	 * 
	 * @return
	 */
	public abstract Action close();

	/**
	 * 增量包
	 * 
	 * @return
	 */
	public abstract List<Patch> dones();

	/**
	 * 备份
	 * 
	 * @return
	 */
	public abstract List<Backup> backups();

	/**
	 * 补丁
	 * 
	 * @author lifeng
	 */
	public static class Patch {
		private String name;
		private String remarks;
		private String createDate;

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

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		/**
		 * 创建补丁对象
		 * 
		 * @param name
		 * @param remarks
		 * @param createDate
		 * @return
		 */
		public static Patch newPatch(String name, String remarks, String createDate) {
			Patch patch = new Patch();
			patch.setName(name);
			patch.setRemarks(remarks);
			patch.setCreateDate(createDate);
			return patch;
		}
	}

	/**
	 * 备份
	 * 
	 * @author lifeng
	 */
	public static class Backup {

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
}
