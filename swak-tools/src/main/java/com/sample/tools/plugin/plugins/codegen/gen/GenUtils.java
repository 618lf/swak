package com.sample.tools.plugin.plugins.codegen.gen;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.io.Files;
import com.swak.freemarker.TemplateMarker;
import com.swak.utils.IOUtils;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.utils.time.DateUtils;

/**
 * 代码生成工具
 * 
 * @author lifeng
 * @date 2020年6月7日 下午3:07:02
 */
@SuppressWarnings("deprecation")
public class GenUtils {

	/**
	 * 生成代码
	 */
	public static void genCode(Scheme scheme) {

		// 获取模板
		List<Template> templates = getTemplates("shop_final");

		// 获取模型数据
		Map<String, Object> model = GenUtils.getDataModel(scheme);

		// 循环创建
		for (Template template : templates) {
			GenUtils.genToFile(template, model);
		}
	}

	// 生成具体的代码
	private static String genToFile(Template template, Map<String, Object> model) {
		try {
			File srcFile = new File(System.getProperty("user.home") + "/Desktop");
			String fileName = new StringBuilder(srcFile.getAbsolutePath())
					.append(StringUtils.replaceEach(TemplateMarker.me().useContent(template.getFilePath(), model),
							new String[] { "//", "/", "." },
							new String[] { File.separator, File.separator, File.separator }))
					.append(TemplateMarker.me().useContent(template.getFileName(), model)).toString();
			String content = TemplateMarker.me().useContent(template.getContent(), model);

			File createFile = new File(fileName);
			createFile.getParentFile().mkdirs();
			createFile.delete();
			createFile.createNewFile();
			Files.write(content, createFile, StandardCharsets.UTF_8);
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取模板
	 * 
	 * @param type
	 * @return
	 */
	private static List<Template> getTemplates(String type) {

		// 所有模板
		List<Template> templates = Lists.newArrayList();

		// Service
		Template template = new Template();
		template.setName("service");
		template.setFilePath("/${packageName}/service/");
		template.setFileName("${className}Service.java");
		template.setContent(readFile(type, "service.ftl"));
		templates.add(template);

		// dao
		template = new Template();
		template.setName("dao");
		template.setFilePath("/${packageName}/dao/");
		template.setFileName("${className}Dao.java");
		template.setContent(readFile(type, "dao.ftl"));
		templates.add(template);

		// mapper
		template = new Template();
		template.setName("mapper");
		template.setFilePath("/${packageName}/dao/");
		template.setFileName("${tableName?lower_case}.Mapper.xml");
		template.setContent(readFile(type, "mapper.ftl"));
		templates.add(template);

		// entity
		template = new Template();
		template.setName("entity");
		template.setFilePath("/${packageName}/entity/");
		template.setFileName("${className}.java");
		template.setContent(readFile(type, "entity.ftl"));
		templates.add(template);
		return templates;

	}

	// 读取文件
	private static String readFile(String type, String fileName) {
		try {
			InputStream is = GenUtils.class.getResourceAsStream(type + "/" + fileName);
			List<String> lines = IOUtils.readLines(is, "UTF-8");
			StringBuilder sb = new StringBuilder();
			for (String line : lines) {
				sb.append(line).append("\r\n");
			}
			if (is != null) {
				is.close();
			}
			return sb.toString();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 将字段名转为属性名
	 * 
	 * @param property USER_NAME --- > userName
	 * @return
	 */
	private static void prepareColumn(TableColumn column) {
		String _dbtype = StringUtils.upperCase(StringUtils.substringBefore(column.getDbType(), "("));
		String _javaType = "";
		String _jdbcType = "";
		if ("ARRAY".equals(_dbtype)) {
			_jdbcType = "ARRAY";
			_javaType = StringUtils.substringAfterLast(Object.class.getName(), ".");
		} else if ("BIGINT".equals(_dbtype)) {
			_jdbcType = "BIGINT";
			_javaType = StringUtils.substringAfterLast(Long.class.getName(), ".");
		} else if ("BINARY".equals(_dbtype)) {
			_jdbcType = "BINARY";
			_javaType = "byte[]";
		} else if ("BIT".equals(_dbtype)) {
			_jdbcType = "BIT";
			_javaType = StringUtils.substringAfterLast(Boolean.class.getName(), ".");
		} else if ("BLOB".equals(_dbtype)) {
			_jdbcType = "BLOB";
			_javaType = "byte[]";
		} else if ("TEXT".equals(_dbtype)) {
			_jdbcType = "LONGVARCHAR";
			_javaType = StringUtils.substringAfterLast(String.class.getName(), ".");
		} else if ("LONGTEXT".equals(_dbtype)) {
			_jdbcType = "LONGVARCHAR";
			_javaType = StringUtils.substringAfterLast(String.class.getName(), ".");
		} else if ("MEDIUMTEXT".equals(_dbtype)) {
			_jdbcType = "LONGVARCHAR";
			_javaType = StringUtils.substringAfterLast(String.class.getName(), ".");
		} else if ("BOOLEAN".equals(_dbtype)) {
			_jdbcType = "BOOLEAN";
			_javaType = StringUtils.substringAfterLast(Boolean.class.getName(), ".");
		} else if ("CHAR".equals(_dbtype)) {
			_jdbcType = "CHAR";
			_javaType = StringUtils.substringAfterLast(String.class.getName(), ".");
		} else if ("CLOB".equals(_dbtype)) {
			_jdbcType = "CLOB";
			_javaType = StringUtils.substringAfterLast(String.class.getName(), ".");
		} else if ("DATALINK".equals(_dbtype)) {
			_jdbcType = "DATALINK";
			_javaType = StringUtils.substringAfterLast(Object.class.getName(), ".");
		} else if ("DATE".equals(_dbtype)) {
			_jdbcType = "DATE";
			_javaType = Date.class.getName();
		} else if ("DATETIME".equals(_dbtype)) {
			_jdbcType = "TIMESTAMP";
			_javaType = Date.class.getName();
		} else if ("DECIMAL".equals(_dbtype)) { // 需要获取长度,小数点数
			_jdbcType = "DECIMAL";
			_javaType = BigDecimal.class.getName();
		} else if ("DISTINCT".equals(_dbtype)) {
			_jdbcType = "DISTINCT";
			_javaType = StringUtils.substringAfterLast(Object.class.getName(), ".");
		} else if ("DOUBLE".equals(_dbtype)) {
			_jdbcType = "DOUBLE";
			_javaType = StringUtils.substringAfterLast(Double.class.getName(), ".");
		} else if ("FLOAT".equals(_dbtype)) {
			_jdbcType = "FLOAT";
			_javaType = StringUtils.substringAfterLast(Double.class.getName(), ".");
		} else if ("INTEGER".equals(_dbtype)) {
			_jdbcType = "INTEGER";
			_javaType = StringUtils.substringAfterLast(Integer.class.getName(), ".");
		} else if ("LONGVARBINARY".equals(_dbtype)) {
			_jdbcType = "LONGVARBINARY";
			_javaType = "byte[]";
		} else if ("LONGVARCHAR".equals(_dbtype)) {
			_jdbcType = "LONGVARCHAR";
			_javaType = StringUtils.substringAfterLast(String.class.getName(), ".");
		} else if ("NULL".equals(_dbtype)) {
			_jdbcType = "NULL";
			_javaType = StringUtils.substringAfterLast(Object.class.getName(), ".");
		} else if ("NUMERIC".equals(_dbtype)) { // 需要获取长度,小数点数
			_jdbcType = "NUMERIC";
			_javaType = BigDecimal.class.getName();
		} else if ("REAL".equals(_dbtype)) {
			_jdbcType = "REAL";
			_javaType = StringUtils.substringAfterLast(Double.class.getName(), ".");
		} else if ("REF".equals(_dbtype)) {
			_jdbcType = "REF";
			_javaType = StringUtils.substringAfterLast(Object.class.getName(), ".");
		} else if ("SMALLINT".equals(_dbtype)) {
			_jdbcType = "SMALLINT";
			_javaType = StringUtils.substringAfterLast(Short.class.getName(), ".");
		} else if ("STRUCT".equals(_dbtype)) {
			_jdbcType = "STRUCT";
			_javaType = StringUtils.substringAfterLast(Object.class.getName(), ".");
		} else if ("TIME".equals(_dbtype)) {
			_jdbcType = "TIME";
			_javaType = Date.class.getName();
		} else if ("TIMESTAMP".equals(_dbtype)) {
			_jdbcType = "TIMESTAMP";
			_javaType = Date.class.getName();
		} else if ("TINYINT".equals(_dbtype)) {
			_jdbcType = "TINYINT";
			_javaType = StringUtils.substringAfterLast(Byte.class.getName(), ".");
		} else if ("VARBINARY".equals(_dbtype)) {
			_jdbcType = "VARBINARY";
			_javaType = "byte[]";
		} else if ("VARCHAR".equals(_dbtype)) {
			_jdbcType = "VARCHAR";
			_javaType = StringUtils.substringAfterLast(String.class.getName(), ".");
		} else if ("NVARCHAR".equals(_dbtype)) {
			_jdbcType = "VARCHAR";
			_javaType = StringUtils.substringAfterLast(String.class.getName(), ".");
		} else if ("MEDIUMINT".equals(_dbtype)) {
			_jdbcType = "MEDIUMINT";
			_javaType = StringUtils.substringAfterLast(Integer.class.getName(), ".");
		} else if ("INT".equals(_dbtype)) {
			_jdbcType = "INTEGER";
			_javaType = StringUtils.substringAfterLast(Integer.class.getName(), ".");
		}

		// 设置对应的类型
		column.setJdbcType(_jdbcType);
		column.setJavaType(_javaType);
		column.setJavaField(StringUtils.convertColumn2Property(column.getName()));
	}

	// 生成模型数据
	private static Map<String, Object> getDataModel(Scheme scheme) {
		Map<String, Object> model = Maps.newHashMap();
		model.put("packageName", scheme.getPackageName());
		model.put("functionName", StringUtils.lowerCaseFirstOne(scheme.getFunctionName()));
		model.put("moduleName", StringUtils.substringAfterLast(scheme.getPackageName(), "."));

		// 功能名的大小
		model.put("className", StringUtils.upperCaseFirstOne(scheme.getFunctionName()));
		model.put("tableName", StringUtils.upperCase(scheme.getTable().getName()));
		model.put("pk", scheme.getTable().getPkJavaType());

		// table数据
		model.put("table", scheme.getTable());
		model.put("firstStringField", scheme.getTable().getFisrtStringField());

		// 日期
		model.put("date", DateUtils.getTodayStr());

		// 处理列
		List<TableColumn> columns = scheme.getTable().getColumns();
		for (TableColumn column : columns) {
			prepareColumn(column);
		}
		return model;
	}
}
