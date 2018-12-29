package com.swak.excel.impl;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.swak.excel.ExcelExecuter;
import com.swak.excel.ExcelUtils;
import com.swak.excel.ImportResult;
import com.swak.excel.impl.DefaultExcelExecuter.ExcelRow;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

/**
 * 默认的excel执行器 --- 单例模式
 * 
 * @author liFeng 2014年9月22日
 */
public class DefaultExcelExecuter implements ExcelExecuter<ExcelRow> {

	/**
	 * iStartRow 从0开始 column 也是从0开始
	 */
	@Override
	public ImportResult<ExcelRow> execute(Sheet sheet) {
		ImportResult<ExcelRow> result = new ImportResult<ExcelRow>(sheet.getSheetName());
		int iStartRow = sheet.getFirstRowNum();
		int iEndRow = sheet.getLastRowNum();
		try {
			for (int i = iStartRow, j = iEndRow; i <= j; i++) {
				processRow(result, i, sheet.getRow(i));
				if (!result.getSuccess() && result.getErrors() != null && result.getErrors().size() >= 20) {
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

	private Boolean processRow(ImportResult<ExcelRow> result, int iRow, Row row) throws Exception {
		int _row = iRow, _cell = 0;
		try {
			Boolean isEmptyRow = Boolean.TRUE;
			if (row == null) {
				return Boolean.TRUE;
			}
			ExcelRow excelRow = new ExcelRow(iRow);
			for (int i = row.getFirstCellNum(), j = row.getLastCellNum(); i < j; i++) {
				_cell = i;
				Object tempValue = getCellValue(row.getCell(i));
				String cellvalue = StringUtils
						.trimToNull(tempValue == null ? StringUtils.EMPTY : String.valueOf(tempValue));
				if (StringUtils.isNotBlank(cellvalue)) {
					isEmptyRow = Boolean.FALSE;
				}
				excelRow.add(ExcelCol.me(_row, _cell, cellvalue).merged(this.hasMerged(row.getCell(i))));
			}
			if (isEmptyRow) {
				return Boolean.TRUE;
			}
			result.addSucessRow(excelRow);
			return Boolean.TRUE;
		} catch (Exception e) {
			result.addError(_row, ExcelUtils.indexToColumn(_cell + 1), e.getMessage());
			return Boolean.TRUE;
		}
	}

	/**
	 * Excel Row 数据
	 * 
	 * @author lifeng
	 */
	public static class ExcelRow {

		private final int row;
		private final List<ExcelCol> cols;

		public ExcelRow(int row) {
			this.row = row;
			this.cols = Lists.newArrayList();
		}

		public int getRow() {
			return row;
		}

		public List<ExcelCol> getCols() {
			return cols;
		}

		public ExcelRow add(ExcelCol col) {
			this.cols.add(col);
			return this;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("{");
			for (ExcelCol col : cols) {
				sb.append(col.toString()).append(",");
			}
			sb.deleteCharAt(sb.length() - 1).append("}\n");
			return sb.toString();
		}
	}

	/**
	 * Excel Col 数据
	 * 
	 * @author lifeng
	 */
	public static class ExcelCol {

		private final int row;
		private final int col;
		private final String name;
		private final Object value;
		private Merged merged;

		public ExcelCol(int row, int col, Object value) {
			this.row = row;
			this.col = col;
			this.name = ExcelUtils.indexToColumn(col + 1);
			this.value = value;
		}

		public ExcelCol merged(CellRangeAddress range) {
			if (range != null) {
				merged = new Merged();
				merged.firstRow = range.getFirstRow();
				merged.lastRow = range.getLastRow();
				merged.firstCol = range.getFirstColumn();
				merged.lastCol = range.getLastColumn();
			}
			return this;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}

		public Merged getMerged() {
			return merged;
		}

		public boolean isMerged() {
			if (merged != null && (merged.firstRow != this.row || merged.firstCol != this.col)) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("");
			sb.append(this.name).append(this.row + 1).append("=").append(this.value);
			if (merged != null) {
				sb.append(merged.toString());
			}
			return sb.toString();
		}

		public static ExcelCol me(int row, int col, Object value) {
			return new ExcelCol(row, col, value);
		}
	}

	// 合并的属性
	public static class Merged {

		private int firstRow;
		private int lastRow;
		private int firstCol;
		private int lastCol;

		public int getFirstRow() {
			return firstRow;
		}

		public void setFirstRow(int firstRow) {
			this.firstRow = firstRow;
		}

		public int getLastRow() {
			return lastRow;
		}

		public void setLastRow(int lastRow) {
			this.lastRow = lastRow;
		}

		public int getFirstCol() {
			return firstCol;
		}

		public void setFirstCol(int firstCol) {
			this.firstCol = firstCol;
		}

		public int getLastCol() {
			return lastCol;
		}

		public void setLastCol(int lastCol) {
			this.lastCol = lastCol;
		}

		public int getRowSpan() {
			return lastRow - firstRow + 1;
		}

		public int getColSpan() {
			return lastCol - firstCol + 1;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("");
			sb.append("(").append(firstRow).append(",").append(firstCol).append(")").append(",");
			sb.append("(").append(lastRow).append(",").append(lastCol).append(")");
			return sb.toString();
		}
	}
}