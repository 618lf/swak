package com.sample.tools.template;

import java.util.List;
import java.util.Properties;

import com.sample.tools.plugin.plugins.codegen.gen.GenUtils;
import com.sample.tools.plugin.plugins.codegen.gen.Scheme;
import com.sample.tools.plugin.plugins.codegen.gen.Table;
import com.sample.tools.plugin.plugins.codegen.gen.TableColumn;
import com.swak.Constants;
import com.swak.config.freemarker.FreeMarkerProperties;
import com.swak.freemarker.FreeMarkerConfigurationFactory;
import com.swak.freemarker.FreeMarkerConfigurer;
import com.swak.freemarker.TemplateMarker;
import com.swak.utils.Lists;

/**
 * 模板测试
 * 
 * @author lifeng
 * @date 2020年6月7日 下午5:00:14
 */
public class TemplateTest {

	/**
	 * 返回 Scheme
	 * 
	 * @return
	 */
	public static Scheme getScheme() {
		Scheme scheme = new Scheme();
		Table table = new Table();

		List<TableColumn> columns = Lists.newArrayList();
		TableColumn column = new TableColumn();
		column.setName("ID");
		column.setIsPk(Constants.YES);
		column.setDbType("BIGINT");
		columns.add(column);

		column = new TableColumn();
		column.setName("NAME");
		column.setDbType("VARCHAR");
		columns.add(column);

		column = new TableColumn();
		column.setName("CREATE_TIME");
		column.setDbType("DATETIME");
		columns.add(column);

		table.setName("SYS_DICT");
		table.setColumns(columns);
		scheme.setTable(table);

		scheme.setPackageName("com.tmt.test");
		scheme.setFunctionName("dict");
		return scheme;
	}

	public static void initTemplateMaker() {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		applyProperties(configurer);
		new TemplateMarker(configurer.getConfiguration());
	}

	protected static void applyProperties(FreeMarkerConfigurationFactory factory) {
		FreeMarkerProperties properties = new FreeMarkerProperties();
		factory.setTemplateLoaderPaths(properties.getTemplateLoaderPath());
		factory.setPreferFileSystemAccess(properties.isPreferFileSystemAccess());
		factory.setDefaultEncoding(Constants.DEFAULT_ENCODING.name());
		Properties settings = getDefaultProperties();
		settings.putAll(properties.getSettings());
		factory.setFreemarkerSettings(settings);
		factory.setFreemarkerVariables(properties.getVariables());
	}

	private static Properties getDefaultProperties() {
		Properties settings = new Properties();
		settings.put("defaultEncoding", Constants.DEFAULT_ENCODING.name());
		settings.put("url_escaping_charset", Constants.DEFAULT_ENCODING.name());
		settings.put("locale", "zh_CN");
		settings.put("template_update_delay", "0");
		settings.put("tag_syntax", "auto_detect");
		settings.put("whitespace_stripping", "true");
		settings.put("classic_compatible", "true");
		settings.put("number_format", "0");
		settings.put("boolean_format", "true,false");
		settings.put("datetime_format", "yyyy-MM-dd HH:mm:ss");
		settings.put("date_format", "yyyy-MM-dd");
		settings.put("time_format", "HH:mm:ss");
		settings.put("object_wrapper", "freemarker.ext.beans.BeansWrapper");
		return settings;
	}

	public static void main(String[] args) {

		// 初始化模板
		initTemplateMaker();

		// 返回模式
		Scheme scheme = getScheme();

		// 生成代码
		GenUtils.genCode(scheme);
	}
}
