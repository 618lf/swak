package com.swak.excel;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;

import com.swak.entity.ColumnMapper;
import com.swak.excel.impl.DefaultExcelExecuter;

/**
 * 默认的 Excel Mapping
 * 
 * @author lifeng
 * @param <T>
 */
public abstract class AbstractExcelMapper<T> implements IExcelMapper<T>, IConverter<T> {

	protected Class<T> clazz;
	protected Multimap<String, ColumnMapper> rowMapper = null;
	protected IConverter<T> converter;

	@Override
	public Boolean returnWhenError() {
		return Boolean.FALSE;
	}

	@Override
	public abstract Class<T> getTargetClass();

	@Override
	public T receive(Map<String, Object> valueMap) {
		return (T) this.doConvert(valueMap, this.getTargetClass());
	}

	@Override
	public T doConvert(Map<String, Object> valueMap, Class<T> clazz) {
		return this.getReceiver().doConvert(valueMap, clazz);
	}

	public ImportResult<T> getExcelData(Sheet sheet) {
		return DefaultExcelExecuter.getInstance().getExcelData(this, sheet);
	}

	// 初始化
	protected abstract void initRowMapper();

	@Override
	public Iterable<ColumnMapper> getColumnMappers(String column) {
		if (rowMapper == null) {
			rowMapper = new Multimap<String, ColumnMapper>();
			this.initRowMapper();
		}
		return rowMapper.get(column);
	}

	public IConverter<T> getReceiver() {
		if (converter == null) {
			converter = new DefaulConverter<T>();
		}
		return converter;
	}

	/**
	 * 默认的转换器
	 * 
	 * @author lifeng
	 * @param <T>
	 */
	public static class DefaulConverter<T> implements IConverter<T> {
		
		@SuppressWarnings("unchecked")
		@Override
		public T doConvert(Map<String, Object> valueMap, Class<T> clazz) {
			if (clazz == HashMap.class) {
				return (T) valueMap;
			} else {
				T obj = null;
				try {
					obj = (T) clazz.newInstance();
					BeanUtils.copyProperties(obj, valueMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return obj;
			}
		}
	}
}