package com.tmt.manage.operation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.tmt.manage.widgets.MD5s;
import com.tmt.manage.widgets.theme.upgrade.Patch;

/**
 * 需要操作的文件
 * 
 * @author lifeng
 */
public class OpsFile {

	// zip 中的文件夹
	public static final String MD5 = "MD5";
	public static final String SQL = "SQLS";
	public static final String JAR = "JAR";
	public static final String LIB = "LIBS";
	public static final String STATIC = "STATICS";
	public static final String CONFIG = "CONFIGS";
	public static final String VER = "VERSION";
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
	 * 获取文件中的sql内容
	 * 
	 * @return
	 * @throws IOException
	 * @throws ZipException
	 */
	public List<OpsEntry> statics() throws Exception {
		return content(STATIC);
	}
	
	/**
	 * 获取文件中的sql内容
	 * 
	 * @return
	 * @throws IOException
	 * @throws ZipException
	 */
	public List<OpsEntry> configs() throws Exception {
		return content(CONFIG);
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
					name = name.substring(type.length());
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
	 * 创建ZIP压缩文件
	 * 
	 * @throws Exception
	 */
	public File compress() throws Exception {

		// 源文件目录
		File makeDir = this.patch().getFile();

		// 临时的压缩文件
		File zipFile = new File(makeDir.getParentFile(), makeDir.getName() + ".zip");

		// 制作压缩文件
		this.compress(makeDir, zipFile);

		// 写 MD5
		return this.sign(makeDir, zipFile);
	}

	// md5 签名
	private File sign(File makeDir, File tempFile) throws Exception {

		// 先签名
		this.patch().setFile(tempFile);
		String md5 = MD5s.encode(this.all().getData(), SALT.getBytes());

		// 源文件
		ZipFile source = new ZipFile(tempFile);

		// 真实的文件
		File zipFile = new File(tempFile.getParentFile(),
				tempFile.getName().substring(0, tempFile.getName().length() - 4) + ":" + this.version(makeDir) + ":"
						+ this.date() + ".zip");
		ZipOutputStream target = new ZipOutputStream(new FileOutputStream(zipFile));
		try {

			// 先复制内容
			this.copy(source, target);

			// 再修改文件
			ZipEntry entry = new ZipEntry(MD5);
			target.putNextEntry(entry);
			target.write(md5.getBytes());
			target.closeEntry();
			
			// 返回最新的文件
			return zipFile;
		} finally {
			source.close();
			target.flush();
			target.close();
			tempFile.delete();
		}
	}

	// 版本号
	private String version(File makeDir) throws IOException {
		File version = new File(makeDir, VER);
		if (version.exists() && version.isFile()) {
			String _version = new String(Files.readAllBytes(version.toPath()), "utf-8");
			if (_version != null) {
				String vString = _version.substring(_version.indexOf("cur:") + 4);
				return vString.trim();
			}
		}
		return "1.0.0";
	}

	// 日期
	private String date() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(new Date());
	}

	// 复制内容
	private void copy(ZipFile source, ZipOutputStream target) throws IOException {
		Enumeration<? extends ZipEntry> entries = source.entries();
		while (entries.hasMoreElements()) {
			ZipEntry e = entries.nextElement();
			target.putNextEntry(e);
			if (!e.isDirectory()) {
				copy(source.getInputStream(e), target);
			}
			target.closeEntry();
		}
	}

	// 复制文件流
	private void copy(InputStream input, OutputStream output) throws IOException {
		int bytesRead = -1;
		byte[] buffer = new byte[512];
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	// 制作压缩文件
	private void compress(File makeDir, File zipFile) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		try {
			File[] files = makeDir.listFiles();
			for (File file : files) {
				this.compress(out, file);
			}
		} finally {
			out.flush();
			out.close();
		}
	}

	// 输出zip文件
	private void compress(ZipOutputStream out, File parent) throws IOException {
		this.compress(out, parent, parent.getName());
	}

	// 输出zip文件
	private void compress(ZipOutputStream out, File parent, String path) throws IOException {
		if (parent.isDirectory()) {
			ZipEntry entry = new ZipEntry(path + File.separator);
			out.putNextEntry(entry);
			File[] files = parent.listFiles();
			for (File file : files) {
				this.compress(out, file, path + File.separator + file.getName());
			}
		} else if (parent.isFile()) {
			out.putNextEntry(new ZipEntry(path));
			out.write(Files.readAllBytes(parent.toPath()));
			out.closeEntry();
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
