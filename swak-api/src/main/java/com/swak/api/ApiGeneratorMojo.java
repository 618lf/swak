package com.swak.api;

import com.swak.Constants;
import com.swak.doc.Api;
import com.swak.utils.IOUtils;
import com.swak.utils.JsonMapper;
import com.swak.utils.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

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
    @Parameter(defaultValue = "ApiDoc")
    private String apiDocFileName;
    @Parameter(defaultValue = "com")
    private String apiDocPackage;

    @Override
    public void execute() throws MojoExecutionException {
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
     * @param file    目标文件
     * @param content 文件内容
     * @throws IOException io异常
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
     * @return 模板
     */
    private String loadTemplate() {
        ByteArrayOutputStream bos = null;
        InputStream in = null;
        try {
            bos = new ByteArrayOutputStream();
            in = ApiGeneratorMojo.class.getResourceAsStream("template.jav");
            byte[] buff = new byte[100];
            int size;
            while ((size = in.read(buff)) != -1) {
                bos.write(buff, 0, size);
            }
            return new String(bos.toByteArray(), Constants.DEFAULT_ENCODING);
        } catch (Exception e) {
            LOGGER.error("Load Template Error:", e);
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(in);
        }
        return StringUtils.EMPTY;
    }
}
