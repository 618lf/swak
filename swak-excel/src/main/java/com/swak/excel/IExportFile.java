package com.swak.excel;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 导出类
 * 
 * @author lifeng
 */
public interface IExportFile {
	
	/** 必须的参数 **/
	String EXPORT_FILE_NAME = "EXPORT_FILE_NAME_KEY";//导出的文件名
	String EXPORT_FILE_TITLE = "EXPORT_FILE_TITLE_KEY";//导出的文件标题
	String EXPORT_COLUMNS = "EXPORT_COLUMNS_KEY";//导出的题头
	String EXPORT_VALUES = "EXPORT_VALUES_KEY";//导出的数据
	String TEMPLATE_NAME = "TEMPLATE_NAME_KEY";//格式参考的文件名
	String TEMPLATE_START_ROW = "TEMPLATE_START_ROW";//导出数据（不包括题头）的开始行，Excel中默认是0开始
	String EXPORTS_PARAM = "export.";//导出的参数
	
	/** 自定义样式 **/
	String CUSTEM_CELL_STYLE_OBJ = "可以设置自定义样式"; // 支持
	
	//导出临时目录,模版文件路径
	String DEFAULT_TEMPLATE_NAME = "defaultTemplate.xls";//格式参考的文件名
	String EXPORT_TEMPLATE_PATH = "excel" + File.separator;
	String CELL_STYLE_NAMES = "CELL_STYLE_NAMES";
	Integer MAX_ROWS = 65535;
	
	//标准的文件格式名
	String XLS = ".xls";
	String ZIP = ".zip";
	
	/**
	 * 导出 file
	 * @param data
	 * @return
	 */
	File build(Map<String, Object> data);
	
	/**
	 * 导出 file
	 * @param data
	 * @return
	 */
	List<File> buildExcels(Map<String, Object> data);
	
	/**
	 * 创建zip 文件
	 * @param files
	 * @param data
	 * @return
	 */
	File buildZip(List<File> files, Map<String, Object> data);
}