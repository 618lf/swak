package com.tmt.manage.widgets.theme.upgrade;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.tmt.manage.widgets.Xmls;
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
	public abstract List<Patch> patchs();

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
	
	/**
	 * 日志
	 * 
	 * @author lifeng
	 */
	@XmlRootElement(name="l")
	public static class Log {
		private String name;
		private String remarks;
		private String time;
		@XmlElement(name = "n")
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		@XmlElement(name = "r")
		public String getRemarks() {
			return remarks;
		}
		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}
		@XmlElement(name = "t")
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		
		/**
		 * 格式化
		 */
		@XmlTransient
		public String format() {
			return Xmls.toXml(this) + "\r\n";
		}
		
		/**
		 * 创建日志
		 * @param name
		 * @param remarks
		 * @return
		 */
		public static Log newLog(String name, String remarks) {
			Log log = new Log();
			log.setName(name);
			log.setRemarks(remarks);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			log.setTime(df.format(new Date()));
			return log;
		}
		
		/**
		 * 解析
		 */
		public static Log parse(String xml) {
			return Xmls.fromXml(xml, Log.class);
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
