package com.swak.api;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API 运行模板
 * 
 * @author lifeng
 */
@Mojo(name = "ApiGenerate", defaultPhase = LifecyclePhase.INSTALL)
public class ApiGeneratorMojo extends AbstractMojo {

	private static final Logger LOGGER = LoggerFactory.getLogger("ApiGeneratorMojo");

	@Parameter(defaultValue = "${project.basedir}", required = true, readonly = true)
	private File baseDir;
	@Parameter(defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
	private File sourceDirectory;
	@Parameter(defaultValue = "target/classes")
	private String classPathDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		URL[] urls;
		try {
			File classpathDir = new File(baseDir, classPathDir);
			URL url = classpathDir.toURI().toURL();
			urls = new URL[] { url };

		} catch (MalformedURLException e) {
			e.printStackTrace();
			LOGGER.error("Can't load class path:" + classPathDir);
			throw new MojoExecutionException("Load class path fail:" + classPathDir, e);
		}

		LOGGER.info("Use classpath:" + Arrays.toString(urls));
	}
}
