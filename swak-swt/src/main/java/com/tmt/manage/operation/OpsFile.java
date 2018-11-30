package com.tmt.manage.operation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.tmt.manage.widgets.theme.upgrade.UpgraderTheme.Patch;

/**
 * 需要操作的文件
 * 
 * @author lifeng
 */
public class OpsFile {

	// zip 中的文件夹
	final String MD5 = "MD5";
	final String SQL = "SQL";
	final String JAR = "JAR";
	final String LIB = "LIB";
	final String SALT = "SWAK";

	// 需要操作的文件
	private final Patch file;
	private final StringBuilder error;

	public OpsFile(Patch zipFile) {
		this.file = zipFile;
		this.error = new StringBuilder();
	}
	
	/**
	 * 补丁文件
	 * 
	 * @param msg
	 */
	public Patch patch() {
		return this.file;
	}

	/**
	 * 添加失败信息
	 * 
	 * @param msg
	 */
	public void error(String msg) {
		this.error.append(msg);
	}

	/**
	 * 返回错误信息
	 * 
	 * @return
	 */
	public String error() {
		return this.error.toString();
	}

	/**
	 * 是否继续
	 * 
	 * @return
	 */
	public boolean continuAbled() {
		if (this.error.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获取文件中的MD5内容
	 * 
	 * @return
	 * @throws IOException
	 * @throws ZipException
	 */
	public OpsEntry md5() throws Exception {
		List<OpsEntry> oentrys = this.content(MD5);
		return oentrys.size() > 0 ? oentrys.get(0) : null;
	}

	/**
	 * 获取文件中的sql内容
	 * 
	 * @return
	 * @throws IOException
	 * @throws ZipException
	 */
	public List<OpsEntry> sqls() throws Exception {
		return content(SQL);
	}

	/**
	 * 获取启动jar
	 * 
	 * @return
	 * @throws IOException
	 * @throws ZipException
	 */
	public OpsEntry jar() throws Exception {
		List<OpsEntry> oentrys = this.content(JAR);
		return oentrys.size() > 0 ? oentrys.get(0) : null;
	}

	/**
	 * 获取文件中的lib内容
	 * 
	 * @return
	 * @throws IOException
	 * @throws ZipException
	 */
	public List<OpsEntry> libs() throws Exception {
		return content(LIB);
	}

	/**
	 * 所有的内容
	 * 
	 * @return
	 * @throws IOException
	 */
	public OpsEntry all() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipFile zip = new ZipFile(file.getFile());
		try {
			Enumeration<?> entries = zip.entries();
			ZipEntry entry = null;
			while (entries.hasMoreElements()) {
				entry = (ZipEntry) entries.nextElement();
				String name = entry.getName();
				if (!name.startsWith(MD5) && !entry.isDirectory()) {
					InputStream data = zip.getInputStream(entry);
					int read = 0;
					byte[] buffer = new byte[512];
					while ((read = data.read(buffer)) >= 0) {
						bos.write(buffer, 0, read);
					}
					buffer = null;
				}
			}
			return new OpsEntry(null, bos.toByteArray());
		} finally {
			zip.close();
			bos.close();
		}
	}

	// 操作项
	private List<OpsEntry> content(String type) throws Exception {
		List<OpsEntry> oentrys = new ArrayList<>();
		ZipFile zip = new ZipFile(file.getFile());
		try {
			Enumeration<?> entries = zip.entries();
			ZipEntry entry = null;
			while (entries.hasMoreElements()) {
				entry = (ZipEntry) entries.nextElement();
				String name = entry.getName();
				if (name.startsWith(type) && !entry.isDirectory()) {
					oentrys.add(this.content(zip.getInputStream(entry), name));
				}
			}
			return oentrys;
		} finally {
			zip.close();
		}
	}

	// 单个操作项
	private OpsEntry content(InputStream data, String name) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			int read = 0;
			byte[] buffer = new byte[512];
			while ((read = data.read(buffer)) >= 0) {
				bos.write(buffer, 0, read);
			}
			buffer = null;
			return new OpsEntry(name, bos.toByteArray());
		} finally {
			bos.close();
		}
	}

	/**
	 * 操作明细
	 * 
	 * @author lifeng
	 */
	public static class OpsEntry {
		private final String name;
		private final byte[] data;

		public OpsEntry(String name, byte[] data) {
			this.name = name;
			this.data = data;
		}

		public String getName() {
			return name;
		}

		public byte[] getData() {
			return data;
		}
	}

	/**
	 * 返回操作文件
	 * 
	 * @param zipFile
	 * @return
	 */
	public static OpsFile ops(Patch file) {
		return new OpsFile(file);
	}
}
