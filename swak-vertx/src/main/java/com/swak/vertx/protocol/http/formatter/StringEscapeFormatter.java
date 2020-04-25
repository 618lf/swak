package com.swak.vertx.protocol.http.formatter;

import com.swak.utils.StringUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * 字符串格式化
 *
 * @author: lifeng
 * @date: 2020/3/29 19:34
 */
public class StringEscapeFormatter implements Converter<String, String> {

    /**
     * 格式化(不需要这么严格，直接去掉脚本就行了)
     */
    @Override
    public String convert(String source) {
        return removeScript(source.trim());
    }

    /**
     * 删除 javaScript 脚本
     */
    private String removeScript(String input) {
        if (StringUtils.isEmpty(input)) {
            return StringUtils.EMPTY;
        }
        return input.replaceAll("<script.*?</script>", "");
    }
}