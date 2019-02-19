package com.swak.excel;

import java.io.File;

import com.swak.excel.impl.DefaultExportFile;

/**
 * 导出类
 * 
 * @author lifeng
 */
public interface ExportFile {

	/**
	 * 导出 file
	 * 
	 * @param data
	 * @return
	 */
	File build();
	
	/**
	 * 默认的export file
	 * @return
	 */
	public static DefaultExportFile def(File dir) {
		return new DefaultExportFile(dir);
	}
}