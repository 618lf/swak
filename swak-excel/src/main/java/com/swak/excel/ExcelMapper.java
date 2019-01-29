package com.swak.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

import com.swak.entity.ColumnMapper;

/**
 * 导入的配置项，读取Excel 的配置项
 * 
 * @author liFeng 2014年9月22日
 */
public interface ExcelMapper<T> {

	/**
	 * 读取Excel 的起始行
	 * 
	 * @return
	 */
	int getStartRow();

	/**
	 * 返回当有错误时
	 * 
	 * @return
	 */
	Boolean returnWhenError();

	/**
	 * 通过对应的列得到 对应的列映射
	 * 
	 * @param column
	 * @return
	 */
	List<ColumnMapper> getColumnMappers(String column);

	/**
	 * 得到Excel的数据
	 * 
	 * @param sheet
	 * @return
	 */
	ImportResult<T> read(Sheet sheet);
	
	/**
	 * 得到类型转化器
	 * 
	 * @return
	 */
	T convert(Map<String, Object> value);

}
