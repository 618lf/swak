package com.swak.excel.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.swak.entity.ColumnMapper;
import com.swak.entity.DataType;
import com.swak.excel.ExcelUtils;
import com.swak.excel.ExcelValidateUtils;
import com.swak.excel.IExcelExecuter;
import com.swak.excel.IExcelMapper;
import com.swak.excel.ImportResult;
import com.swak.utils.StringUtils;
import com.swak.utils.time.DateUtils;

/**
 * 默认的excel执行器 --- 单例模式
 * 
 * @author liFeng 2014年9月22日
 */
public class DefaultExcelExecuter implements IExcelExecuter {

	private static DefaultExcelExecuter EXECUTER = new DefaultExcelExecuter();

	private DefaultExcelExecuter() {
	}

	public static IExcelExecuter getInstance() {
		return EXECUTER;
	}

	/**
	 * iStartRow 从0开始 column 也是从0开始
	 */
	@Override
	public <T> ImportResult<T> getExcelData(IExcelMapper<T> mapper, Sheet sheet) {
		ImportResult<T> result = new ImportResult<T>(sheet.getSheetName());
		int iStartRow = mapper.getStartRow() - 1;
		int iEndRow = sheet.getLastRowNum();
		try {
			for (int i = iStartRow, j = iEndRow; i <= j; i++) {
				Boolean bFlag = processRow(mapper, result, i, sheet.getRow(i));
				if (mapper.returnWhenError() && !bFlag) {
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

	private <T> Boolean processRow(IExcelMapper<T> mapper, ImportResult<T> result, int iRow, Row row) throws Exception {
		int _row = iRow, _cell = 0;
		try {
			Boolean isEmptyRow = Boolean.TRUE; // 是否空行-- 空行不处理，但也不提示错误
			if (row == null) {
				return Boolean.TRUE;
			}
			Map<String, Object> valueMap = new HashMap<String, Object>();
			for (int i = row.getFirstCellNum(), j = row.getLastCellNum(); i < j; i++) {

				// 记录当前列号
				_cell = i;

				Iterable<ColumnMapper> columnMappers = mapper.getColumnMappers(ExcelUtils.indexToColumn(i + 1));
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
			result.addSucessRow(mapper.receive(valueMap));
			return Boolean.TRUE;
		} catch (Exception e) {
			result.addError(_row, ExcelUtils.indexToColumn(_cell + 1), e.getMessage());
			return Boolean.TRUE;
		}
	}

	/**
	 * cell 的值
	 * 
	 * @param cell
	 * @param columnMapper
	 * @return
	 * @throws Exception
	 */
	private Object getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
		String strValue = null;
		if (isDateCell(cell)) { // 日期格式
			return cell.getDateCellValue();
		} else if (CellType.NUMERIC == cell.getCellType()) {// 数字
			strValue = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
		} else if (CellType.BOOLEAN == cell.getCellType()) {
			strValue = String.valueOf(cell.getBooleanCellValue());
		} else if (CellType.BLANK == cell.getCellType()) {
			strValue = "";
		} else if (CellType.FORMULA == cell.getCellType()) {
			evaluator.evaluateFormulaCell(cell);
			strValue = String.valueOf(cell.getNumericCellValue());
		} else if (CellType.ERROR == cell.getCellType()) {
			strValue = String.valueOf(cell.getErrorCellValue());
		} else {
			cell.setCellType(CellType.STRING);
			strValue = cell.getStringCellValue();
		}
		// 处理特殊字符,去掉最后的.0
		if ((CellType.FORMULA == cell.getCellType() || CellType.NUMERIC == cell.getCellType())
				&& StringUtils.endsWith(strValue, "\\.[0]?")) {
			strValue = StringUtils.substringBefore(strValue, ".");
		}
		// 处理特殊的字符
		if (StringUtils.isNotBlank(strValue)) {
			// strValue = strValue.replace((char)12288, ' ');//全角空白
			strValue = strValue.trim();// 去掉首位空白
		}
		return strValue;
	}

	/**
	 * 是否是日期列
	 * 
	 * @param cell
	 * @return
	 */
	private Boolean isDateCell(Cell cell) {
		if (cell.getCellType() == CellType.NUMERIC) {
			double value = cell.getNumericCellValue();
			if (ExcelUtils.isValidExcelDate(value)) {
				CellStyle style = cell.getCellStyle();
				if (style != null) {
					int i = style.getDataFormat();
					String f = style.getDataFormatString();
					return ExcelUtils.isADateFormat(i, f);
				}
			}
		}
		return Boolean.FALSE;
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
}
