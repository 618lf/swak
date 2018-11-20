package com.swak.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.entity.DataType;
import com.swak.entity.LabelVO;
import com.swak.entity.Result;
import com.swak.utils.IOUtils;
import com.swak.utils.Lists;

/**
 * 
 * @ClassName: ExcelUtils
 * @author 李锋
 * @date 2013-4-26 下午10:18:05
 */
public abstract class ExcelUtils {

	private ExcelUtils() {
	}

	/**
	 * 返回Excel的列序号集合
	 * 
	 * @return
	 */
	public static List<LabelVO> getExcelColumns(int length) {
		int _length = length == 0 ? 40 : length;
		List<LabelVO> columns = Lists.newArrayList();
		for (int i = 1; i < _length; i++) {
			String key = indexToColumn(i);
			columns.add(new LabelVO(key, key));
		}
		return columns;
	}

	/**
	 * 返回可选的数据类型
	 * 
	 * @return
	 */
	public static List<LabelVO> getDataTypes() {
		List<LabelVO> columns = Lists.newArrayList();
		for (DataType dataType : DataType.values()) {
			columns.add(new LabelVO(dataType.name(), dataType.getName()));
		}
		return columns;
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
	 * 从路径加载Excel
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws BiffException
	 */
	public static Workbook loadExcelFile(String filePath) throws FileNotFoundException, IOException {
		return loadExcelFile(new File(filePath));
	}

	/**
	 * 从文件加载Excel
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws BiffException
	 */
	public static Workbook loadExcelFile(File file) throws IOException {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new IOException("指定的Excel数据文件不存在", e);
		}
		return loadExcelFile(fileInputStream);
	}

	/**
	 * 从流加载Excel
	 * 
	 * @param inputStream
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws BiffException
	 */
	public static Workbook loadExcelFile(InputStream inputStream) throws IOException {
		try {
			Workbook book = null;
			if (!(inputStream.markSupported())) {
				inputStream = new PushbackInputStream(inputStream, 8);
			}
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

	/**
	 * 获取数据
	 * 
	 * @param templateId
	 * @param obj
	 * @param file
	 * @return
	 */
	public static <T> Result fetchObjectFromMapper(AbstractExcelMapper<T> mapper, File file) {
		try {
			Workbook book = ExcelUtils.loadExcelFile(file);
			return fetchObjectFromTemplate(mapper, book, false);
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
	public static <T> Result fetchObjectFromTemplate(AbstractExcelMapper<T> mapper, Workbook book, boolean first) {
		Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
		try {
			List<T> objects = Lists.newArrayList();
			int sheets = first ? 1 : book.getNumberOfSheets();
			for (int i = 0; i < sheets; i++) {
				if (!book.isSheetHidden(i)) {
					ImportResult<T> result = mapper.getExcelData(book.getSheetAt(i));
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
}