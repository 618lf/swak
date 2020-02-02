package com.tmt.gen.utils;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.Constants;
import com.swak.utils.IOUtils;
import com.swak.utils.JaxbMapper;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.utils.time.DateUtils;
import com.tmt.gen.entity.Category;
import com.tmt.gen.entity.Config;
import com.tmt.gen.entity.Scheme;
import com.tmt.gen.entity.Table;
import com.tmt.gen.entity.TableColumn;
import com.tmt.gen.entity.Template;
import com.tmt.gen.service.TableService;

@SuppressWarnings("deprecation")
public class GenUtils {

	private static String REMOVE = "_RM_";
	private static String srcFilePath = null;
	private static TableService tableService = null;
	private static Logger logger = LoggerFactory.getLogger(GenUtils.class);

	private static Config config = null;

	// 读取文件
	private static String readFile(String fileName) {
		try {
			InputStream is = GenUtils.class.getResourceAsStream(fileName);
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
			logger.warn("Error file convert: {}", e.getMessage());
		}
		return null;
	}

	// 读取文件并转为对象
	private static <T> T file2Object(String fileName, Class<T> clazz) {
		String lines = readFile(fileName);
		return (T) JaxbMapper.fromXml(lines, clazz);
	}

	/**
	 * 获取代码生成配置对象
	 * 
	 * @return
	 */
	public static Config getConfig() {
		if (config == null) {
			config = file2Object("config.xml", Config.class);
		}
		return config;
	}

	/**
	 * 将字段名转为属性名
	 * 
	 * @param property
	 *            USER_NAME --- > userName
	 * @return
	 */
	public static void convertDbType2JavaTypes(TableColumn column) {
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
	}

	// 设置默认的显示类型和校验类型
	public static void convertDbType2ShowTypes(TableColumn column) {
		String _dbtype = StringUtils.upperCase(StringUtils.substringBefore(column.getDbType(), "("));
		String _showType = "";
		String _checkType = "";
		if ("TINYINT".equals(_dbtype)) {
			_showType = "y_n_r";
		} else if ("DATE".equals(_dbtype)) {
			_showType = "date";
			_checkType = "date";
		} else if ("DATETIME".equals(_dbtype)) {
			_showType = "date";
			_checkType = "date";
		}

		String javaField = column.getJavaField();
		if (StringUtils.endsWith(javaField, "userId") || StringUtils.endsWith(javaField, "officeId")
				|| StringUtils.endsWith(javaField, "areaId")) {
			_showType = "treeselect";
		} else if (StringUtils.endsWith(javaField, "userIds") || StringUtils.endsWith(javaField, "officeIds")
				|| StringUtils.endsWith(javaField, "areaIds") || StringUtils.endsWith(javaField, "parentId")) {
			_showType = "tanselect";
		} else if (StringUtils.indexOf(javaField, "icon") != -1) {
			_showType = "iconselect";
		} else if (StringUtils.indexOf(javaField, "images") != -1) {
			_showType = "mutilimg";
		} else if (StringUtils.indexOf(javaField, "image") != -1) {
			_showType = "singleimg";
		}
		column.setShowType(_showType);
		column.setCheckType(_checkType);
	}

	/**
	 * 获取所有的模版
	 * 
	 * @param config
	 * @param category
	 * @return
	 */
	public static List<Template> getTemplateList(String category) {
		config = GenUtils.getConfig();
		List<Template> templates = Lists.newArrayList();
		if (config != null && config.getCategoryList() != null && category != null) {
			for (Category e : config.getCategoryList()) {
				if (category.equals(e.getValue())) {
					List<String> list = e.getTemplate();
					if (list != null) {
						for (String s : list) {
							if (StringUtils.startsWith(s, Category.CATEGORY_REF)) {
								templates.addAll(getTemplateList(StringUtils.replace(s, Category.CATEGORY_REF, "")));
							} else {
								Template template = file2Object(s, Template.class);
								if (template != null) {
									template.setContent(readFile(template.getTemplatePath()));
									templates.add(template);
								}
							}
						}
					}
				}
			}
		}
		return templates;
	}

	/**
	 * 获取要生成的表
	 * 
	 * @param scheme
	 * @return
	 */
	private static Table getTableWithColumns(Scheme scheme) {
		return tableService.getWithColumns(scheme.getGenTableId());
	}

	/**
	 * 生成代码
	 * 
	 * @param scheme
	 * @return
	 */
	public static Boolean genCode(Scheme scheme) {
		// 要生成的表的配置
		Table table = GenUtils.getTableWithColumns(scheme);
		scheme.setTable(table);

		// 获取模型数据
		Map<String, Object> model = GenUtils.getDataModel(scheme);

		// 对应的模版
		List<Template> templates = GenUtils.getTemplateList(scheme.getCategory());
		for (Template template : templates) {
			if (!"viewTableSelect".equals(template.getName())) {
				GenUtils.genToFile(template, model);
			} else if (scheme.getTableSelect() != null && Constants.YES == scheme.getTableSelect()) {
				GenUtils.genToFile(template, model);
			}
		}
		return Boolean.TRUE;
	}

	// 生成模型数据
	private static Map<String, Object> getDataModel(Scheme scheme) {
		Map<String, Object> model = Maps.newHashMap();
		model.put("schemeCategory", scheme.getCategory());
		model.put("packageName", scheme.getPackageName());
		model.put("moduleName", scheme.getModuleName());
		model.put("subModuleName", scheme.getSubModuleName());
		model.put("functionName", Kits.lowerCaseFirstOne(scheme.getFunctionName()));
		model.put("functionNameSimple", scheme.getFunctionNameSimple());
		model.put("functionAuthor", scheme.getFunctionAuthor());
		model.put("functionVersion", DateUtils.getTodayStr());

		// 功能名的大小
		model.put("className", Kits.upperCaseFirstOne(scheme.getFunctionName()));
		model.put("tableName", StringUtils.upperCase(scheme.getTable().getName()));
		model.put("pk", scheme.getTable().getPkJavaType());

		// 增强功能
		model.put("isImport", scheme.getIsImport());
		model.put("isExport", scheme.getIsExport());
		model.put("isExport_Import", (scheme.getIsImport() != null && scheme.getIsExport() != null
				&& scheme.getIsImport() == 1 && scheme.getIsExport() == 1) ? "1" : "0");
		model.put("treeSelect", scheme.getTreeSelect());
		model.put("tableSelect", scheme.getTableSelect());

		// table数据
		model.put("table", scheme.getTable());
		model.put("firstStringField", scheme.getTable().getFisrtStringField());

		// 日期
		model.put("date", DateUtils.getTodayStr());
		model.put("author", scheme.getAuthor());

		// 纠正相关问题
		if (scheme.getModuleName().equals(scheme.getSubModuleName())
				|| StringUtils.isBlank(scheme.getSubModuleName())) {
			model.put("subModuleName", REMOVE);
		}

		// 模块对象前缀
		if (REMOVE.equals(model.get("subModuleName"))) {
			model.put("prefix", model.get("moduleName"));
		} else {
			model.put("prefix", model.get("subModuleName"));
		}
		return model;
	}

	// 得到src目录
	private static File getSrcFolder(Template template) {
		if (srcFilePath == null) {
			String parentpath = new File(".").getAbsolutePath();
			File parentFile = new File(parentpath);
			if (parentFile != null && parentFile.exists()) {
				parentFile = parentFile.getParentFile();
			}
			File srcFile = new File(parentFile, "src");
			if (!srcFile.exists()) {
				srcFile = new File(parentFile, "java");
			}
			srcFilePath = srcFile.getAbsolutePath();
		}
		return new File(srcFilePath);
	}

	// 生成具体的代码
	private static String genToFile(Template template, Map<String, Object> model) {
		try {
			File srcFile = getSrcFolder(template);
			String fileName = new StringBuilder(srcFile.getAbsolutePath())
					.append(StringUtils.replaceEach(Kits.processContent(template.getFilePath(), model),
							new String[] { "//", "/", "." },
							new String[] { File.separator, File.separator, File.separator }))
					.append(Kits.processContent(template.getFileName(), model)).toString();
			fileName = StringUtils.replace(fileName, File.separator + REMOVE, "");
			logger.debug(" fileName === " + fileName);

			String content = Kits.processContent(template.getContent(), model);

			// 处理问题
			content = StringUtils.replaceEach(content, new String[] { "/" + REMOVE, "." + REMOVE },
					new String[] { "", "" });
			Kits.deleteFile(fileName);

			if (Kits.createFile(fileName)) {
				Kits.writeFile(new File(fileName), content, Constants.DEFAULT_ENCODING);
				logger.debug(" file create === " + fileName);
				return fileName;
			} else {
				logger.debug(" file extents === " + fileName);
				return fileName;
			}
		} catch (Exception e) {
			logger.error("生成文件出错: ", e);
			return null;
		}
	}
}
