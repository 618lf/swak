package com.tmt.gen.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

import com.swak.utils.IOUtils;
import com.swak.utils.StringUtils;

/**
 * 工具
 * 
 * @author lifeng
 */
public class Kits {

	/**
	 * 将字段名转为属性名 USER_NAME --- > userName
	 * 
	 * @param property
	 *            USER_NAME --- > userName
	 * @return
	 */
	public static String convertColumn2Property(String column) {
		StringBuilder property = new StringBuilder();
		String[] columns = column.split("_");
		for (int i = 0; i < columns.length; i++) {
			String s = StringUtils.lowerCase(columns[i]);
			if (i != 0) {
				s = Kits.upperCaseFirstOne(s);
			}
			property.append(s);
		}
		return property.toString();
	}

	/**
	 * 首字母转小写
	 * 
	 * @param s
	 * @return
	 */
	public static String lowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	/**
	 * 首字母转大写
	 * 
	 * @param s
	 * @return
	 */
	public static String upperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
		}
	}

	/**
	 * 生成数据错误
	 * 
	 * @param content
	 * @param model
	 * @return
	 */
	public static String processContent(String content, Map<String, Object> model) {
		StringWriter out = new StringWriter();
//		try {
//			FreeMarkerConfigurer configurer = SpringContextHolder.getBean(FreeMarkerConfigurer.class);
//			new freemarker.template.Template("template", new StringReader(content), configurer.getConfiguration())
//					.process(model, out);
//		} catch (Exception localIOException) {
//			throw new BaseRuntimeException("生成数据错误", localIOException);
//		}
		return out.toString();
	}

	/**
	 * 写文件
	 * 
	 * @param file
	 * @param content
	 * @param encoding
	 * @throws IOException
	 */
	public static void writeFile(File file, String content, Charset encoding) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file, true);
			out.write(content.getBytes(encoding));
			out.close();
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 
	 * 删除单个文件
	 * 
	 * @param fileName
	 *            被删除的文件名
	 * @return 如果删除成功，则返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * 创建单个文件
	 * 
	 * @param descFileName
	 *            文件名，包含路径
	 * @return 如果创建成功，则返回true，否则返回false
	 */
	public static boolean createFile(String descFileName) {
		File file = new File(descFileName);
		if (file.exists()) {
			return false;
		}
		if (descFileName.endsWith(File.separator)) {
			return false;
		}
		if (!file.getParentFile().exists()) {
			// 如果文件所在的目录不存在，则创建目录
			if (!file.getParentFile().mkdirs()) {
				return false;
			}
		}

		// 创建文件
		try {
			if (file.createNewFile()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
}
