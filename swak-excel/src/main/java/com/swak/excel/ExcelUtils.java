package com.swak.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.entity.ColumnMapper;
import com.swak.entity.Result;
import com.swak.excel.impl.DefaultExcelMapper;
import com.swak.exception.BaseRuntimeException;
import com.swak.utils.IOUtils;
import com.swak.utils.Lists;
import com.swak.zip.ZipEntry;
import com.swak.zip.ZipOutputStream;

/**
 * 
 * @ClassName: ExcelUtils
 * @author 李锋
 * @date 2013-4-26 下午10:18:05
 */
public abstract class ExcelUtils {

	private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

	private ExcelUtils() {
	}

	/**
	 * 将Excel列头转换为序号，如B->2
	 * 
	 * @param column
	 * @return
	 */
	public static int columnToIndex(String column) {
		int index = 0;
		char[] chars = column.toUpperCase().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			index += ((int) chars[i] - (int) 'A' + 1) * (int) Math.pow(26, chars.length - i - 1);
		}
		return index;
	}

	/**
	 * 将Excel列序号转换为列头，如2->B
	 * 
	 * @param index
	 * @return
	 */
	public static String indexToColumn(int index) {
		String rs = "";
		do {
			index--;
			rs = ((char) (index % 26 + (int) 'A')) + rs;
			index = (int) ((index - index % 26) / 26);
		} while (index > 0);
		return rs;
	}

	/**
	 * 是否是日期列
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean isValidExcelDate(double value) {
		return DateUtil.isValidExcelDate(value);
	}

	/**
	 * 是否是日期列
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean isADateFormat(int i, String f) {
		return DateUtil.isADateFormat(i, f);
	}

	// ########### Load Excel ################

	/**
	 * 从流加载Excel
	 * 
	 * @param inputStream
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws BiffException
	 */
	public static Workbook load(InputStream inputStream) throws IOException {
		try {
			Workbook book = null;
			inputStream = FileMagic.prepareToCheckMagic(inputStream);
			if (FileMagic.valueOf(inputStream) == FileMagic.OLE2) {
				book = new HSSFWorkbook(inputStream);
			} else if (FileMagic.valueOf(inputStream) == FileMagic.OOXML) {
				book = new XSSFWorkbook(OPCPackage.open(inputStream));
			}
			return book;
		} catch (IOException e) {
			throw new IOException("加载Excel数据文件异常", e);
		} catch (InvalidFormatException e) {
			throw new IOException("加载Excel数据文件异常", e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	// ########### 读取Excel 数据 ################
	
	/**
	 * 获取数据
	 * 
	 * @param templateId
	 * @param obj
	 * @param file
	 * @return
	 */
	public static <T> Result read(DefaultExcelMapper<T> mapper, File file) {
		return read(mapper, file, false);
	}
	
	/**
	 * 获取数据
	 * 
	 * @param templateId
	 * @param obj
	 * @param file
	 * @return
	 */
	public static <T> Result read(DefaultExcelMapper<T> mapper, File file, boolean first) {
		try {
			return read(mapper, new FileInputStream(file), first);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.error("数据导入错误");
	}

	/**
	 * 获取数据
	 * 
	 * @param templateId
	 * @param obj
	 * @param file
	 * @return
	 */
	public static <T> Result read(DefaultExcelMapper<T> mapper, InputStream file) {
		return read(mapper, file, false);
	}

	/**
	 * 获取数据
	 * 
	 * @param templateId
	 * @param obj
	 * @param file
	 * @return
	 */
	public static <T> Result read(DefaultExcelMapper<T> mapper, InputStream file, boolean first) {
		try {
			Workbook book = ExcelUtils.load(file);
			List<T> objects = Lists.newArrayList();
			int sheets = first ? 1 : book.getNumberOfSheets();
			for (int i = 0; i < sheets; i++) {
				if (!book.isSheetHidden(i)) {
					ImportResult<T> result = mapper.read(book.getSheetAt(i));
					if (result != null && result.getSuccess()) {
						List<T> _objects = result.getSucessRows();
						if (null != _objects) {
							objects.addAll(_objects);
						}

						// debug
						if (logger.isDebugEnabled()) {
							int size = _objects != null ? _objects.size() : 0;
							String name = book.getSheetName(i);
							logger.debug("read {} rows from {}", size, name);
						}
					} else {
						return Result.error(result.getMsg(), result.getErrors());
					}
				}
			}
			return Result.success(objects);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.error("数据导入错误,未读取到数据");
	}

	// ########### 写 Excel 数据 ################

	/**
	 * 创建Excel 文件
	 * 
	 * @param fileName
	 * @param title
	 * @param columns
	 * @param vaules
	 * @param templatenName
	 * @param startRow
	 * @return
	 */
	public static File write(String fileName, String title, List<ColumnMapper> columns,
			List<Map<String, Object>> vaules, String templateName, Integer startRow) {
		try {
			return ExportFile.def().templateName(templateName).startRow(startRow).fileName(fileName).fileTitle(title)
					.columns(columns).values(vaules).build();
		} catch (Exception e) {
			return null;
		}
	}
	
	// ########### ZIP ################
	/**
	 * 创建zip 文件
	 * 
	 * @param files
	 * @param data
	 * @return
	 */
	public static File buildZip(List<File> files, File zipFile) {
		InputStream objInputStream = null;
		ZipOutputStream objZipOutputStream = null;
		try {
			objZipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
			objZipOutputStream.setEncoding("UTF-8"); 
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