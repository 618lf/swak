package com.swak.excel;

import org.apache.poi.ss.usermodel.Sheet;

import com.swak.excel.impl.DefaultExcelExecuter;

/**
 * 执行器
 * 
 * @author liFeng 2014年9月22日
 */
public interface ExcelExecuter {

	/**
	 * 得到excel的数据
	 * 
	 * @param sheet
	 * @return
	 */
	<T> ImportResult<T> execute(ExcelMapper<T> mapper, Sheet sheet);
	
	/**
	 * 默认的执行器
	 * 
	 * @return
	 */
    static ExcelExecuter def() {
    	return new DefaultExcelExecuter();
    }
}
