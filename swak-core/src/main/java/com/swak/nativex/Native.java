package com.swak.nativex;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.OS;
import com.swak.utils.Lists;

/**
 * 本地工具类
 *
 * @author: lifeng
 * @date: 2020/3/29 12:08
 */
public class Native {

	private static final Logger logger = LoggerFactory.getLogger(Native.class);
	private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
	private static final File WORKDIR = new File(OS.temp());

	/**
	 * 加载本地库文件
	 *
	 * @param name lib 名称
	 * @author lifeng
	 * @date 2020-01-03 10:57:07
	 */
	public static void loadLibrary(String name) {
		String sharedLibName = name + '_' + normalizeArch();
		ClassLoader cl = getClassLoader();
		try {
			load(sharedLibName, cl);
		} catch (UnsatisfiedLinkError e1) {
			try {
				load(name, cl);
			} catch (UnsatisfiedLinkError e2) {
				throw e1;
			}
		}
	}

	/**
	 * Load the given library with the specified {@link ClassLoader}
	 */
	private static void load(String originalName, ClassLoader loader) {
		List<Throwable> suppressed = Lists.newArrayList();
		try {
			// first try to load from java.library.path
			loadLibrary(originalName, false);
			return;
		} catch (Throwable ex) {
			suppressed.add(ex);
			logger.debug("{} cannot be loaded from java.library.path, " + "now trying export to -Djava.io.tmpdir: {}",
					originalName, WORKDIR, ex);
		}
		String libname = System.mapLibraryName(originalName);
		String path = NATIVE_RESOURCE_HOME + libname;
		InputStream in = null;
		OutputStream out = null;
		File tmpFile = null;
		URL url;
		if (loader == null) {
			url = ClassLoader.getSystemResource(path);
		} else {
			url = loader.getResource(path);
		}
		try {
			if (url == null) {
				if (OS.me() == OS.mac) {
					String fileName = path.endsWith(".jnilib") ? NATIVE_RESOURCE_HOME + "lib" + originalName + ".dynlib"
							: NATIVE_RESOURCE_HOME + "lib" + originalName + ".jnilib";
					if (loader == null) {
						url = ClassLoader.getSystemResource(fileName);
					} else {
						url = loader.getResource(fileName);
					}
					if (url == null) {
						throw new FileNotFoundException(fileName);
					}
				} else {
					throw new FileNotFoundException(path);
				}
			}
			int index = libname.lastIndexOf('.');
			String prefix = libname.substring(0, index);
			String suffix = libname.substring(index);
			tmpFile = File.createTempFile(prefix, suffix, WORKDIR);
			in = url.openStream();
			out = new FileOutputStream(tmpFile);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			out.flush();
			// Close the output stream before loading the unpacked library,
			// because otherwise Windows will refuse to load it when it's in use by other
			// process.
			closeQuietly(out);
			out = null;
			loadLibrary(tmpFile.getPath(), true);
		} catch (UnsatisfiedLinkError e) {
			try {
				if (tmpFile != null && tmpFile.isFile() && tmpFile.canRead()) {
					// Pass "io.netty.native.workdir" as an argument to allow shading tools to see
					// the string. Since this is printed out to users to tell them what to do next,
					// we want the value to be correct even when shading.
					logger.info(
							"{} exists but cannot be executed even when execute permissions set; "
									+ "check volume for \"noexec\" flag; use -D{}=[path] "
									+ "to set native working directory separately.",
							tmpFile.getPath(), "io.netty.native.workdir");
				}
			} catch (Throwable t) {
				suppressed.add(t);
				logger.debug("Error checking if {} is on a file store mounted with noexec", tmpFile, t);
			}
			// Re-throw to fail the load
			throw e;
		} catch (Exception e) {
			UnsatisfiedLinkError ule = new UnsatisfiedLinkError("could not load a native library: " + originalName);
			ule.initCause(e);
			throw ule;
		} finally {
			closeQuietly(in);
			closeQuietly(out);
			// After we load the library it is safe to delete the file.
			// We delete the file immediately to free up resources as soon as possible,
			// and if this fails fallback to deleting on JVM exit.
			if (tmpFile != null && !tmpFile.delete()) {
				tmpFile.deleteOnExit();
			}
		}
	}

	/**
	 * 系统默认的加载方式
	 *
	 * @param libName  libName
	 * @param absolute 默认加载
	 * @author lifeng
	 * @date 2020-01-03 11:09:55
	 */
	private static void loadLibrary(String libName, boolean absolute) {
		if (absolute) {
			System.load(libName);
		} else {
			System.loadLibrary(libName);
		}
	}

	/**
	 * 系统架构
	 *
	 * @return 系统架构
	 * @author lifeng
	 * @date 2020-01-03 11:00:15
	 */
	static String normalizeArch() {
		String value = System.getProperty("os.arch", "");
		value = value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
		if (value.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
			return "x86_64";
		}
		if (value.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
			return "x86_32";
		}
		if (value.matches("^(ia64|itanium64)$")) {
			return "itanium_64";
		}
		if (value.matches("^(sparc|sparc32)$")) {
			return "sparc_32";
		}
		if (value.matches("^(sparcv9|sparc64)$")) {
			return "sparc_64";
		}
		if (value.matches("^(arm|arm32)$")) {
			return "arm_32";
		}
		if ("aarch64".equals(value)) {
			return "aarch_64";
		}
		if (value.matches("^(ppc|ppc32)$")) {
			return "ppc_32";
		}
		if ("ppc64".equals(value)) {
			return "ppc_64";
		}
		if ("ppc64le".equals(value)) {
			return "ppcle_64";
		}
		if ("s390".equals(value)) {
			return "s390_32";
		}
		if ("s390x".equals(value)) {
			return "s390_64";
		}

		return "unknown";
	}

	/**
	 * 类加载器
	 *
	 * @return 类加载器
	 * @author lifeng
	 * @date 2020-01-03 11:01:17
	 */
	static ClassLoader getClassLoader() {
		if (System.getSecurityManager() == null) {
			return Native.class.getClassLoader();
		} else {
			return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) Native.class::getClassLoader);
		}
	}

	/**
	 * 静默关闭
	 *
	 * @param c 关闭方法
	 * @author lifeng
	 * @date 2020-01-03 11:12:06
	 */
	static void closeQuietly(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException ignore) {
				// ignore
			}
		}
	}
}