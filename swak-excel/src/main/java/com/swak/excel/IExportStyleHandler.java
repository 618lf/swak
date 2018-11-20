package com.swak.excel;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import com.swak.entity.ColumnMapper;

/**
 * 导出样式设置
 * @author root
 *
 */
public interface IExportStyleHandler {

	/**
	 * 设置样式
	 * @param objRow
	 * @param objCell
	 */
	public void addStyle(Workbook  template, Row objRow, Cell objCell, Map<String,Object> rowData, ColumnMapper mapper);
}
