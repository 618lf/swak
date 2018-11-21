package com.swak.excel;

import java.io.File;

import com.swak.excel.impl.DefaultExportFile;

/**
 * 导出类
 * 
 * @author lifeng
 */
public interface ExportFile {

	String EXPORTS_PARAM = "export.";// 导出的参数
	String CUSTEM_CELL_STYLE_OBJ = "可以设置自定义样式"; // 支持
	String DEFAULT_TEMPLATE_NAME = "defaultTemplate.xls";// 格式参考的文件名
	String EXPORT_TEMPLATE_PATH = File.separator + "excel" + File.separator;
	Integer MAX_ROWS = 65535;
	String XLS = ".xls";
	String ZIP = ".zip";

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
	public static DefaultExportFile def() {
		return new DefaultExportFile();
	}
}