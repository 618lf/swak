package com.swak.excel;

import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

import com.swak.entity.ColumnMapper;

/**
 * 导入的配置项，读取Excel 的配置项
 * 
 * @author liFeng 2014年9月22日
 */
public interface IExcelMapper<T> {

	/**
	 * 读取Excel 的起始行
	 * 
	 * @return
	 */
	public int getStartRow();

	/**
	 * 返回当有错误时
	 * 
	 * @return
	 */
	public Boolean returnWhenError();

	/**
	 * 通过对应的列得到 对应的列映射
	 * 
	 * @param column
	 * @return
	 */
	public Iterable<ColumnMapper> getColumnMappers(String column);

	/**
	 * 得到目标类型
	 * 
	 * @return
	 */
	public Class<?> getTargetClass();

	/**
	 * 得到类型转化器
	 * 
	 * @return
	 */
	public T receive(Map<String, Object> valueMap);

	/**
	 * 得到Excel的数据
	 * 
	 * @param sheet
	 * @return
	 */
	public ImportResult<T> getExcelData(Sheet sheet);

}
