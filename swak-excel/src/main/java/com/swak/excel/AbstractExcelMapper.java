package com.swak.excel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

import com.swak.entity.ColumnMapper;
import com.swak.excel.impl.DefaultExcelExecuter;
import com.swak.utils.JsonMapper;

/**
 * 默认的 Excel Mapping
 * 
 * @author lifeng
 * @param <T>
 */
public abstract class AbstractExcelMapper<T> implements ExcelMapper<T> {

	protected Class<T> clazz;
	protected Multimap<String, ColumnMapper> rowMapper = null;
	protected Converter<T> converter;

	@Override
	public Boolean returnWhenError() {
		return Boolean.FALSE;
	}

	/**
	 * 默认的转换数据
	 */
	@Override
	public T convert(Map<String, Object> valueMap) {
		return (T) this.getConverter().doConvert(valueMap, this.getTargetClass());
	}

	/**
	 * 读取excel数据
	 */
	public ImportResult<T> read(Sheet sheet) {
		return DefaultExcelExecuter.getInstance().getExcelData(this, sheet);
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

	/**
	 * 默认的转换器
	 * @return
	 */
	public Converter<T> getConverter() {
		if (converter == null) {
			converter = new DefaulConverter<T>();
		}
		return converter;
	}

	// 初始化
	protected abstract List<ColumnMapper> getRowMapper();
	protected abstract Class<T> getTargetClass();
	
	/**
	 * 默认的转换器
	 * 
	 * @author lifeng
	 * @param <T>
	 */
	public static class DefaulConverter<T> implements Converter<T> {

		@SuppressWarnings("unchecked")
		@Override
		public T doConvert(Map<String, Object> valueMap, Class<T> clazz) {
			if (clazz == HashMap.class) {
				return (T) valueMap;
			} else {
				String jsons = JsonMapper.toJson(valueMap);
				return JsonMapper.fromJson(jsons, clazz);
			}
		}
	}
}