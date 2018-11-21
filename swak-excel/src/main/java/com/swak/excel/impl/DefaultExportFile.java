package com.swak.excel.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.swak.entity.ColumnMapper;
import com.swak.entity.DataType;
import com.swak.excel.ExcelUtils;
import com.swak.excel.ExportFile;
import com.swak.excel.StyleHandler;
import com.swak.exception.BaseRuntimeException;
import com.swak.utils.FileUtils;
import com.swak.utils.IOUtils;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.zip.ZipEntry;
import com.swak.zip.ZipOutputStream;

/**
 * 默认的导出工具类
 * 
 * @author lifeng
 */
public class DefaultExportFile implements ExportFile {

	private String fileName;
	private String fileTitle;
	private List<ColumnMapper> columns;
	private List<Map<String, Object>> values;
	private String templateName;
	private Integer startRow;
	private Map<String, CellStyle> cellStyles;
	private StyleHandler styleHandler;

	public DefaultExportFile fileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public DefaultExportFile fileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
		return this;
	}

	public DefaultExportFile columns(List<ColumnMapper> columns) {
		this.columns = columns;
		return this;
	}

	public DefaultExportFile values(List<Map<String, Object>> values) {
		this.values = values;
		return this;
	}

	public DefaultExportFile templateName(String templateName) {
		this.templateName = templateName;
		return this;
	}

	public DefaultExportFile startRow(Integer startRow) {
		this.startRow = startRow;
		return this;
	}

	public DefaultExportFile cellStyles(Map<String, CellStyle> cellStyles) {
		this.cellStyles = cellStyles;
		return this;
	}

	public DefaultExportFile styleHandler(StyleHandler styleHandler) {
		this.styleHandler = styleHandler;
		return this;
	}

	/**
	 * 根据参数导出文件
	 */
	public File build() {
		List<File> files = this.buildExcels();
		if (files.size() <= 1) {
			return files.get(0);
		}
		return buildZip(files);
	}

	/**
	 * 根据参数导出文件
	 */
	private List<File> buildExcels() {

		/**
		 * 校验参数
		 */
		if (!this.checkDataAttr()) {
			return null;
		}

		/**
		 * 模板文件
		 */
		File templateFile = FileUtils.classpath(EXPORT_TEMPLATE_PATH + templateName);
		
		/**
		 * 导出
		 */
		List<File> files = Lists.newArrayList();
		List<Map<String, Object>> datas = this.values;
		int maxSize = MAX_ROWS - this.startRow;
		if (datas != null && datas.size() > maxSize) {
			List<List<Map<String, Object>>> pDatas = Lists.partition(datas, maxSize);
			for (List<Map<String, Object>> value : pDatas) {
				File outFile = createFile(templateFile, value);
				files.add(outFile);
			}
		} else {
			// 生成文件
			File outFile = createFile(templateFile, datas);
			files.add(outFile);
		}

		// 返回 excel 文件
		return files;
	}

	// 创建文件
	private File createFile(File templateFile, List<Map<String, Object>> values) {
		File outFile = FileUtils.tempFile( this.fileName + XLS);
		FileOutputStream out = null;
		try {
			Workbook template = ExcelUtils.load(templateFile);
			out = FileUtils.out(outFile);
			writeData(template, values);
			template.write(out);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BaseRuntimeException(e.getMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
		return outFile;
	}

	// 写入数据
	public void writeData(Workbook template, List<Map<String, Object>> value) {
		// 写入题头
		writeHeader(template);
		// 写入实际的数据
		writeBody(template, value);
		// 自定义样式
		custemStyle(template);
	}

	// 写入题头
	protected void writeHeader(Workbook template) {
		if (DEFAULT_TEMPLATE_NAME.equals(templateName)) {
			Row objRow = null;
			Cell objCell = null;
			Sheet sheet = template.getSheetAt(0);
			// 写题头
			int iHeader = this.startRow - 1;
			int iRegLeght = 1;
			if (iHeader > 0) {
				objRow = sheet.getRow(iHeader);
				if (objRow == null) {
					objRow = sheet.createRow(iHeader);
					objRow.setHeight((short) 500);
				}
				int iCellIndex = 0;
				// 设置序号
				objCell = objRow.getCell(iCellIndex);
				if (objCell == null) {
					objCell = objRow.createCell(iCellIndex);
					objCell.setCellStyle(getHeaderStyle(template, "template"));
				}
				objCell.setCellValue("序号");
				objCell.setCellStyle(getHeaderStyle(template, "header"));
				iCellIndex++;
				// 具体的数据
				for (ColumnMapper mapper : this.columns) {
					objCell = objRow.getCell(iCellIndex);
					if (objCell == null) {
						objCell = objRow.createCell(iCellIndex);
						objCell.setCellStyle(getHeaderStyle(template, "template"));
					}
					iCellIndex++;
					String value = mapper.getTitle();
					if (StringUtils.isBlank(value) || "null".equals(value)) {
						value = "";
					}
					objCell.setCellValue(value);
					objCell.setCellStyle(getHeaderStyle(template, "header"));
					iRegLeght++;
				}
			}

			int lastCellNum = iRegLeght;
			if (lastCellNum > 14) {
				lastCellNum = 14;
			}
			// 表头
			CellRangeAddress region = new CellRangeAddress(0, 0, 0, lastCellNum - 1);
			sheet.addMergedRegion(region);
			objRow = sheet.getRow(0);
			if (objRow == null) {
				objRow = sheet.createRow(0);
			}
			objCell = objRow.getCell(0);
			if (objCell == null) {
				objCell = objRow.createCell(0);
			}
			objCell.setCellValue(this.fileTitle);
			CellStyle style = objCell.getCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER);
			objCell.setCellStyle(style);
			template.setSheetName(0, this.fileTitle);
			objRow = null;
			objCell = null;
		}
	}

	// 写入实际的数据
	protected void writeBody(Workbook template, List<Map<String, Object>> values) {
		Row objRow = null;
		Cell objCell = null;
		Sheet sheet = template.getSheetAt(0);
		int iPos = this.startRow;
		if (values != null && values.size() != 0) {
			for (int i = 0, j = values.size(); i < j; i++) {
				Map<String, Object> oneData = this.values.get(i);
				objRow = sheet.getRow(i + iPos);
				if (objRow == null) {
					objRow = sheet.createRow(i + iPos);
					objRow.setHeight((short) 500);
				}
				int iCellIndex = 0;
				// 设置序号
				objCell = objRow.getCell(iCellIndex);
				if (objCell == null) {
					objCell = objRow.createCell(iCellIndex);
					objCell.setCellStyle(getCellStyle(template, null));
				}
				objCell.setCellValue(i + 1);
				objCell.setCellType(CellType.NUMERIC);
				iCellIndex++;
				// 具体的数据
				for (ColumnMapper mapper : this.columns) {
					objCell = objRow.getCell(iCellIndex);
					if (objCell == null) {
						objCell = objRow.createCell(iCellIndex);
					}
					iCellIndex++;
					String value = String.valueOf(oneData.get(mapper.getProperty()));
					if (StringUtils.isBlank(value) || "null".equals(value)) {
						value = "";
					}
					// 格式
					if (mapper.getDataType() == DataType.DATE && !StringUtils.isBlank(value)) {
						objCell.setCellValue(value);
					} else if (mapper.getDataType() == DataType.MONEY && !StringUtils.isBlank(value)) {
						objCell.setCellValue(Double.parseDouble(value));
					} else {
						objCell.setCellValue(value);
					}
					objCell.setCellStyle(getCellStyle(template, mapper));
				}
			}
		}
	}

	protected void custemStyle(Workbook template) {
		StyleHandler handler = this.styleHandler;
		if (handler != null) {
			Row objRow = null;
			Cell objCell = null;
			Sheet sheet = template.getSheetAt(0);
			int iPos = this.startRow;
			List<Map<String, Object>> datas = this.values;
			if (datas != null && datas.size() != 0) {
				for (int i = 0, j = this.values.size(); i < j; i++) {
					Map<String, Object> oneData = this.values.get(i);
					objRow = sheet.getRow(i + iPos);
					int iCellIndex = 1;
					// 具体的数据
					for (ColumnMapper mapper : this.columns) {
						objCell = objRow.getCell(iCellIndex);
						handler.addStyle(template, objRow, objCell, oneData, mapper);
						iCellIndex++;
					}
				}
			}
		}
	}

	// 检查相应的参数
	private Boolean checkDataAttr() {
		if (this.columns == null && this.values == null) {
			return Boolean.FALSE;
		}
		if (StringUtils.isBlank(this.templateName)) {
			this.templateName = DEFAULT_TEMPLATE_NAME;
		}
		if (StringUtils.isBlank(this.fileName)) {
			this.fileName = "导出_";
		}
		if (this.startRow == null) {
			this.startRow = 2;
		}
		return Boolean.TRUE;
	}
	
	/**
	 * 默认模板的样式
	 * @param template
	 * @return
	 */
	private Map<String, CellStyle> defaultCellStyles(Workbook template) {
		Map<String, CellStyle> cells = Maps.newHashMap();
		if (DEFAULT_TEMPLATE_NAME.equals(templateName)) {
			Cell objCellTemplate = template.getSheetAt(1).getRow(0).getCell(2);
			CellStyle templateStyle = getWrapCellStyle(objCellTemplate.getCellStyle());
			objCellTemplate = template.getSheetAt(1).getRow(0).getCell(0);
			CellStyle dateStyle = getWrapCellStyle(objCellTemplate.getCellStyle());
			objCellTemplate = template.getSheetAt(1).getRow(0).getCell(1);
			CellStyle moneyStyle = getWrapCellStyle(objCellTemplate.getCellStyle());
			objCellTemplate = template.getSheetAt(1).getRow(0).getCell(4);
			CellStyle headerStyle = getWrapCellStyle(objCellTemplate.getCellStyle());
			cells.put("template", templateStyle);
			cells.put("header", headerStyle);
			cells.put("date", dateStyle);
			cells.put("money", moneyStyle);
		} else {
			int iPos = this.startRow;
			Sheet sheet = template.getSheetAt(0);
			Row objRow = sheet.getRow(iPos);
			if (objRow == null) {
				objRow = sheet.createRow(iPos);
			}
			int iCellIndex = 0;
			Cell objCell = objRow.getCell(iCellIndex);
			if (objCell == null) {
				objCell = objRow.createCell(iCellIndex);
			}
			cells.put("A", getWrapCellStyle(objCell.getCellStyle()));
			iCellIndex++;
			for (ColumnMapper mapper : this.columns) {
				objCell = objRow.getCell(iCellIndex);
				if (objCell == null) {
					objCell = objRow.createCell(iCellIndex);
				}
				CellStyle cellStyle = getWrapCellStyle(objCell.getCellStyle());
				cells.put(mapper.getColumn(), cellStyle);
				iCellIndex++;
			}
		}
		this.cellStyles = cells;
		return cells;
	}
	
	// 默认的样式
	private CellStyle getWrapCellStyle(CellStyle cellStyle) {
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		return cellStyle;
	}

	// 得到单元格的格式(题头)
	protected CellStyle getHeaderStyle(Workbook template, String cellKey) {
		Map<String, CellStyle> cells = this.cellStyles;
		if (cells == null) {
			cells = defaultCellStyles(template);
		}
		return cells.get(cellKey);
	}

	// 得到数据单元格的样式
	protected CellStyle getCellStyle(Workbook template, ColumnMapper mapper) {
		Map<String, CellStyle> cells = this.cellStyles;
		if (cells == null) {
			cells = defaultCellStyles(template);
		}
		if (DEFAULT_TEMPLATE_NAME.equals(templateName)) {
			if (mapper != null && mapper.getDataType() == DataType.DATE) {
				return cells.get("date");
			} else if (mapper != null && mapper.getDataType() == DataType.MONEY) {
				return cells.get("money");
			}
			return cells.get("template");
		} else {
			return cells.get(mapper == null ? "A" : mapper.getColumn());
		}
	}

	/**
	 * 创建zip 文件
	 * 
	 * @param files
	 * @param data
	 * @return
	 */
	private File buildZip(List<File> files) {
		File zipFile = FileUtils.tempFile(this.fileName + ZIP);
		InputStream objInputStream = null;
		ZipOutputStream objZipOutputStream = null;
		try {
			objZipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
			objZipOutputStream.setEncoding("GBK");
			for (File file : files) {
				objZipOutputStream.putNextEntry(new ZipEntry(file.getName()));
				objInputStream = new FileInputStream(file);
				byte[] blobbytes = new byte[10240];
				int bytesRead = 0;
				while ((bytesRead = objInputStream.read(blobbytes)) != -1) {
					objZipOutputStream.write(blobbytes, 0, bytesRead);
				}
				// 重要，每次必须关闭此流，不然下面的临时文件是删不掉的
				if (objInputStream != null) {
					objInputStream.close();
				}
				objZipOutputStream.closeEntry();
			}
			return zipFile;
		} catch (Exception e) {
			throw new BaseRuntimeException(e.getMessage());
		} finally {
			IOUtils.closeQuietly(objInputStream);
			IOUtils.closeQuietly(objZipOutputStream);
		}
	}
}
