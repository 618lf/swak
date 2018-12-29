package com.swak.excel;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.swak.excel.impl.DefaultExcelExecuter;
import com.swak.excel.impl.DefaultExcelExecuter.ExcelRow;
import com.swak.utils.StringUtils;

/**
 * 执行器
 * 
 * @author liFeng 2014年9月22日
 */
public interface ExcelExecuter<T> {

	/**
	 * 得到excel的数据
	 * 
	 * @param sheet
	 * @return
	 */
	ImportResult<T> execute(Sheet sheet);

	/**
	 * cell 的值
	 * 
	 * @param cell
	 * @param columnMapper
	 * @return
	 * @throws Exception
	 */
	default Object getCellValue(Cell cell) {
		if (cell == null) {
			return StringUtils.EMPTY;
		}
		FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
		String strValue = null;
		if (isDateCell(cell)) {
			return cell.getDateCellValue();
		} else if (CellType.NUMERIC == cell.getCellType()) {
			strValue = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
		} else if (CellType.BOOLEAN == cell.getCellType()) {
			strValue = String.valueOf(cell.getBooleanCellValue());
		} else if (CellType.BLANK == cell.getCellType()) {
			strValue = StringUtils.EMPTY;
		} else if (CellType.FORMULA == cell.getCellType()) {
			evaluator.evaluateFormulaCell(cell);
			strValue = String.valueOf(cell.getNumericCellValue());
		} else if (CellType.ERROR == cell.getCellType()) {
			strValue = String.valueOf(cell.getErrorCellValue());
		} else {
			cell.setCellType(CellType.STRING);
			strValue = cell.getStringCellValue();
		}
		if ((CellType.FORMULA == cell.getCellType() || CellType.NUMERIC == cell.getCellType())
				&& StringUtils.endsWith(strValue, "\\.[0]?")) {
			strValue = StringUtils.substringBefore(strValue, ".");
		}
		if (StringUtils.isNotBlank(strValue)) {
			strValue = strValue.trim();
		}
		return strValue;
	}

	/**
	 * 是否是日期列
	 * 
	 * @param cell
	 * @return
	 */
	default Boolean isDateCell(Cell cell) {
		if (cell.getCellType() == CellType.NUMERIC) {
			double value = cell.getNumericCellValue();
			if (DateUtil.isValidExcelDate(value)) {
				CellStyle style = cell.getCellStyle();
				if (style != null) {
					int i = style.getDataFormat();
					String f = style.getDataFormatString();
					return DateUtil.isADateFormat(i, f);
				}
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * 是否是合并列
	 * 
	 * @param cell
	 * @return
	 */
	default CellRangeAddress hasMerged(Cell cell) {
		if (cell == null) {
			return null;
		}
		Sheet sheet = cell.getSheet();
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress region = sheet.getMergedRegion(i);
			if (region.getFirstRow() <= cell.getRowIndex() && region.getLastRow() >= cell.getRowIndex()
					&& region.getFirstColumn() <= cell.getColumnIndex()
					&& region.getLastColumn() >= cell.getColumnIndex()) {
				return region;
			}
		}
		return null;
	}

	/**
	 * 默认的执行器
	 * 
	 * @return
	 */
	static ExcelExecuter<ExcelRow> def() {
		return new DefaultExcelExecuter();
	}
}
