package com.swak.api;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.Constants;
import com.swak.doc.Api;
import com.swak.utils.IOUtils;
import com.swak.utils.JsonMapper;
import com.swak.utils.StringUtils;

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

	// 生成的 api java类的名称
	@Parameter(defaultValue = "ApiDoc")
	private String apiDocFileName;
	@Parameter(defaultValue = "com")
	private String apiDocPackage;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {

			// 源码目录
			String source = StringUtils.replace(sourceDirectory.getPath(), baseDir.getPath(), "");

			// 生成API
			List<Api> apis = ApiConfig.of().addSourcePath(source).build();

			// 创建Api 管理类
			String template = this.loadTemplate();
			template = template.replace("${package}", apiDocPackage);
			template = template.replace("${className}", apiDocFileName);
			template = template.replace("${apis}", JsonMapper.toJson(apis));

			File dir = new File(sourceDirectory, apiDocPackage.replaceAll("\\.", "/"));
			if (dir.isFile()) {
				LOGGER.error(dir.getAbsolutePath() + " is not a directory");
				throw new MojoExecutionException(dir.getAbsolutePath() + " is not a directory");
			}

			if (!dir.exists() && !dir.mkdirs()) {
				LOGGER.error("mkdir " + dir.getAbsolutePath() + " fail");
				throw new MojoExecutionException("mkdir " + dir.getAbsolutePath() + " fail");
			}
			File file = new File(dir, apiDocFileName + ".java");
			this.writeFile(file, template);
			LOGGER.info("create file:" + file.getAbsolutePath());
		} catch (Exception e) {
			LOGGER.error("create file:", e);
			throw new MojoExecutionException("Load source path fail:" + sourceDirectory, e);
		}
	}

	/**
	 * 写文件
	 * 
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	private void writeFile(File file, String content) throws IOException {
		FileOutputStream fos = new FileOutputStream(file, false);
		OutputStreamWriter osw = new OutputStreamWriter(fos, Constants.DEFAULT_ENCODING);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(content);
		IOUtils.closeQuietly(bw);
	}

	/**
	 * 加载模板
	 * 
	 * @return
	 */
	private String loadTemplate() {
		ByteArrayOutputStream buff = null;
		InputStream in = null;
		try {
			buff = new ByteArrayOutputStream();
			in = ApiGeneratorMojo.class.getResourceAsStream("template.jav");
			byte[] _buff = new byte[100];
			int size = 0;
			while ((size = in.read(_buff)) != -1) {
				buff.write(_buff, 0, size);
			}
			return new String(buff.toByteArray(), Constants.DEFAULT_ENCODING);
		} catch (Exception e) {
			LOGGER.error("Load Template Error:", e);
		} finally {
			IOUtils.closeQuietly(buff);
			IOUtils.closeQuietly(in);
		}
		return StringUtils.EMPTY;
	}
}
