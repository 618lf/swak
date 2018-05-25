package com.swak.entity;

import java.io.Serializable;

/**
 * Excel 的列的配置
 * @ClassName: ColumnMapper 
 * @author 李锋
 * @date 2013-4-26 下午09:52:05 
 *
 */
public class ColumnMapper implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int cellIndex;
    private String title;
    private String column;
    private DataType dataType;
    private String dataFormat;
    private String property;
    private String verifyFormat;
    
    public ColumnMapper(){}
    public ColumnMapper(String column, DataType dataType, String property) {
        this.column = column;
        this.dataType = dataType;
        this.property = property;
    }
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDataFormat() {
		return dataFormat;
	}
	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}
    public int getCellIndex() {
		return cellIndex;
	}
	public void setCellIndex(int cellIndex) {
		this.cellIndex = cellIndex;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getVerifyFormat() {
		return verifyFormat;
	}
	public void setVerifyFormat(String verifyFormat) {
		this.verifyFormat = verifyFormat;
	}
	
	/**
	 * 导出列构建
	 * @param title
	 * @param column
	 * @param dataType
	 * @param property
	 * @return
	 */
	public static ColumnMapper buildExpText(String title, String property) {
		return ColumnMapper.build(title, null, DataType.STRING, property);
	}
	
	/**
	 * 导出列构建
	 * @param title
	 * @param column
	 * @param dataType
	 * @param property
	 * @return
	 */
	public static ColumnMapper buildExpNum(String title, String property) {
		return ColumnMapper.build(title, null, DataType.NUMBER, property);
	}
	
	/**
	 * 导出列构建
	 * @param title
	 * @param column
	 * @param dataType
	 * @param property
	 * @return
	 */
	public static ColumnMapper buildExpMoney(String title, String property) {
		return ColumnMapper.build(title, null, DataType.MONEY, property);
	}
	
	/**
	 * 导出列构建
	 * @param title
	 * @param column
	 * @param dataType
	 * @param property
	 * @return
	 */
	public static ColumnMapper buildExpMapper(String title, String column, DataType dataType, String property) {
		return ColumnMapper.build(title, column, dataType, property);
	}
	
	/**
	 * ColumnMapper 构建
	 * @author lifeng
	 */
	public static ColumnMapper build(String title, String column, DataType dataType, String property) {
		ColumnMapper mapper = new ColumnMapper(column, dataType, property);
		mapper.setTitle(title);
		return mapper;
	}
}