package com.swak;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.swak.utils.IOUtils;

/**
 * 版本号
 * 
 * @author lifeng
 * @date 2020年8月27日 上午11:16:50
 */
public final class Version {

	static String version;

	private Version() {
	}

	/**
	 * Return the full version string of the present Spring Boot codebase, or
	 * {@code null} if it cannot be determined.
	 * 
	 * @return the version of Spring Boot or {@code null}
	 * @see Package#getImplementationVersion()
	 */
	public synchronized static String getVersion() {
		if (version == null) {
			version = determineSpringBootVersion();
		}
		return version;
	}

	private static String determineSpringBootVersion() {
		String implementationVersion = Version.class.getPackage().getImplementationVersion();
		if (implementationVersion != null) {
			return implementationVersion;
		}
		CodeSource codeSource = Version.class.getProtectionDomain().getCodeSource();
		if (codeSource == null) {
			return null;
		}
		URL codeSourceLocation = codeSource.getLocation();
		try {
			URLConnection connection = codeSourceLocation.openConnection();
			if (connection instanceof JarURLConnection) {
				return getImplementationVersion(((JarURLConnection) connection).getJarFile());
			}
			File manifestFile = new File(new File(codeSourceLocation.toURI()), "META-INF/MANIFEST.MF");
			return getImplementationVersion(manifestFile);
		} catch (Exception ex) {
			return null;
		}
	}

	private static String getImplementationVersion(JarFile jarFile) throws IOException {
		return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
	}

	private static String getImplementationVersion(File manifestFile) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(manifestFile);
			Manifest manifest = new Manifest(fis);
			return manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
}
