package com.swak.excel;

import java.util.Map;

/**
 * 数据转换器
 * 
 * @ClassName: IConverter
 * @author 李锋
 * @date 2013-4-26 下午09:48:04
 */
public interface IConverter<T> {

	/**
	 * 接收收据
	 * 
	 * @param Excel转换过来的值对象：valueMap
	 * @param 映射对象：excelMapper
	 * @return 实体对象
	 */
	T doConvert(Map<String, Object> valueMap, Class<T> clazz);
}
