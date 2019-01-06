package com.swak.excel.impl;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.swak.entity.ColumnMapper;
import com.swak.entity.DataType;
import com.swak.excel.ExcelExecuter;
import com.swak.excel.ExcelMapper;
import com.swak.excel.ExcelUtils;
import com.swak.excel.ExcelValidateUtils;
import com.swak.excel.ImportResult;
import com.swak.excel.Multimap;
import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.utils.time.DateUtils;

/**
 * 默认的 Excel Mapping
 * 
 * @author lifeng
 * @param <T>
 */
public abstract class DefaultExcelMapper<T> implements ExcelMapper<T>, ExcelExecuter<T> {

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
			String json = JsonMapper.toJson(valueMap);
			return JsonMapper.fromJson(json, this.getTargetClass());
		}
	}

	/**
	 * 读取excel数据
	 */
	@Override
	public ImportResult<T> read(Sheet sheet) {
		return this.execute(sheet);
	}

	/**
	 * iStartRow 从0开始 column 也是从0开始
	 */
	@Override
	public ImportResult<T> execute(Sheet sheet) {
		ImportResult<T> result = new ImportResult<T>(sheet.getSheetName());
		int iStartRow = this.getStartRow() - 1;
		int iEndRow = sheet.getLastRowNum();
		try {
			for (int i = iStartRow, j = iEndRow; i <= j; i++) {
				Boolean bFlag = processRow(result, i, sheet.getRow(i));
				if (this.returnWhenError() && !bFlag) {
					break;
				}
				if (!result.getSuccess() && result.getErrors() != null && result.getErrors().size() >= 20) { // 一次最多显示20条错误信息
					break;
				}
			}
			if (!result.getSuccess()) {
				result.setSucessRows(null);
			}
		} catch (Exception e) {
			result = ImportResult.error(sheet.getSheetName(), e.getMessage());
		}
		return result;
	}

	private Boolean processRow(ImportResult<T> result, int iRow, Row row) throws Exception {
		int _row = iRow, _cell = 0;
		try {
			Boolean isEmptyRow = Boolean.TRUE; // 是否空行-- 空行不处理，但也不提示错误
			if (row == null) {
				return Boolean.TRUE;
			}
			Map<String, Object> valueMap = Maps.newOrderMap();
			for (int i = row.getFirstCellNum(), j = row.getLastCellNum(); i < j; i++) {

				// 记录当前列号
				_cell = i;

				Iterable<ColumnMapper> columnMappers = this.getColumnMappers(ExcelUtils.indexToColumn(i + 1));
				if (columnMappers == null || !columnMappers.iterator().hasNext()) {
					continue;
				}

				Object tempValue = getCellValue(row.getCell(i));
				String cellvalue = null;

				if (tempValue == null) {// 无值
					cellvalue = "";
				} else {
					cellvalue = String.valueOf(tempValue);
				}
				Boolean isDefaultFormat = Boolean.FALSE;
				Iterator<ColumnMapper> it = columnMappers.iterator();
				while (it.hasNext()) {
					ColumnMapper columnMapper = it.next();
					if (tempValue instanceof Date) {
						String pattern = columnMapper.getDataFormat();
						Date date = (Date) tempValue;
						if (StringUtils.isNotBlank(pattern)) {
							cellvalue = DateUtils.getFormatDate(date, pattern);
						} else if (!isDefaultFormat) {
							cellvalue = DateUtils.getFormatDate(date, "yyyy-MM-dd");
							isDefaultFormat = Boolean.TRUE;
						}
					}
					// 去空
					cellvalue = StringUtils.trimToNull(cellvalue);
					// 这列不为空
					if (StringUtils.isNotBlank(cellvalue)) {
						isEmptyRow = Boolean.FALSE;
					}
					// 特殊的验证
					String msg = ExcelValidateUtils.validate(cellvalue, columnMapper.getVerifyFormat());
					if (StringUtils.isNotBlank(msg)) {
						String _column = StringUtils.format("%s[%s]", columnMapper.getTitle(),
								columnMapper.getColumn());
						result.addError(iRow + 1, _column, msg);
						return Boolean.TRUE;
					}
					// 设置的格式的验证支持日期格式化,文本转换 --(只做了文本的格式化)
					tempValue = formatCell(cellvalue, columnMapper);
					valueMap.put(columnMapper.getProperty(), tempValue);
				}
			}
			if (isEmptyRow) { // 空行就返回 -- 但不提示有空行
				return Boolean.TRUE;
			}
			result.addSucessRow(this.convert(valueMap));
			return Boolean.TRUE;
		} catch (Exception e) {
			result.addError(_row, ExcelUtils.indexToColumn(_cell + 1), e.getMessage());
			return Boolean.TRUE;
		}
	}

	// 现阶段只做了文本的转换
	private Object formatCell(String cellvalue, ColumnMapper columnMapper) {
		if (!StringUtils.isNotBlank(cellvalue)) {
			return cellvalue;
		} else if (columnMapper.getDataType() == DataType.STRING
				&& StringUtils.isNotBlank(columnMapper.getDataFormat())) {// 是文本
			// 文本转换
			String pattern = columnMapper.getDataFormat();
			String[] ps = pattern.split(";");
			for (String s : ps) {
				if (cellvalue.equals(s.substring(0, s.indexOf(":")))) {
					cellvalue = s.substring(s.indexOf(":") + 1);
					break;
				}
			}
			return cellvalue;
		} else if (columnMapper.getDataType() == DataType.NUMBER || columnMapper.getDataType() == DataType.MONEY) {
			return Double.valueOf(cellvalue);
		}
		return cellvalue;
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