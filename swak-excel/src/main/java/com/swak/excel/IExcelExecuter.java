package com.swak.excel;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * 执行器
 * 
 * @author liFeng 2014年9月22日
 */
public interface IExcelExecuter {

	/**
	 * 得到excel的数据
	 * 
	 * @param sheet
	 * @return
	 */
	public <T> ImportResult<T> getExcelData(IExcelMapper<T> mapper, Sheet sheet);
}
