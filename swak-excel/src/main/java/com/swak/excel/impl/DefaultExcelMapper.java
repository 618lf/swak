package com.swak.excel.impl;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

import com.swak.entity.ColumnMapper;
import com.swak.excel.ExcelExecuter;
import com.swak.excel.ExcelMapper;
import com.swak.excel.ImportResult;
import com.swak.excel.Multimap;

import net.sf.cglib.beans.BeanMap;

/**
 * 默认的 Excel Mapping
 * 
 * @author lifeng
 * @param <T>
 */
public abstract class DefaultExcelMapper<T> implements ExcelMapper<T> {

	protected Class<T> clazz;
	protected Multimap<String, ColumnMapper> rowMapper = null;

	/**
	 * 当出现错误时是否终止
	 */
	@Override
	public Boolean returnWhenError() {
		return Boolean.FALSE;
	}

	/**
	 * 默认从第三行读取数据
	 */
	@Override
	public int getStartRow() {
		return 3;
	}

	/**
	 * 目标对象 T 的实际类型
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> getTargetClass() {
		if (clazz == null) {
			clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return clazz;
	}

	/**
	 * 默认的转换数据, 使用 CGLIB 的方式转换
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T convert(Map<String, Object> valueMap) {
		if (this.getTargetClass() == HashMap.class) {
			return (T) valueMap;
		} else {
			BeanMap beanMap = BeanMap.create(this.getTargetClass());  
			beanMap.putAll(valueMap);  
			return (T) beanMap.getBean();
		}
	}

	/**
	 * 读取excel数据
	 */
	@Override
	public ImportResult<T> read(Sheet sheet) {
		return ExcelExecuter.def().execute(this, sheet);
	}

	/**
	 * 列映射
	 */
	@Override
	public Iterable<ColumnMapper> getColumnMappers(String column) {
		if (rowMapper == null) {
			rowMapper = new Multimap<String, ColumnMapper>();
			List<ColumnMapper> columns = this.getRowMapper();
			for (ColumnMapper _column : columns) {
				rowMapper.put(_column.getColumn(), _column);
			}
		}
		return rowMapper.get(column);
	}

	// 初始化
	protected abstract List<ColumnMapper> getRowMapper();
}