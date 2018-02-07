package com.swak.mvc.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public class StringEscapeConverter implements Converter<String, String>{
	
	/**
	 * 格式化(不需要这么严格，直接去掉脚本就行了)
	 */
	@Override
	public String convert(String source) {
		return source == null ? null : removeScript(source.trim());
	}
	
	/**
	 * 删除 javaScript 脚本
	 * @return
	 */
	public String removeScript(String input) {
		if(StringUtils.isEmpty(input)) {
		   return "";
		}
		return input.replaceAll("<script.*?</script>", "");
	}
}