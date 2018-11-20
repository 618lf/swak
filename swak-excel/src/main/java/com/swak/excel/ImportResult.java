package com.swak.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: ImportResult
 * @author 李锋
 * @date 2013-4-26 下午09:02:50
 * 
 */
public class ImportResult<T> {

	private Boolean success = Boolean.TRUE;
	private Integer totalCount = 0;
	private List<Error> errors;
	private List<T> sucessRows;
	private Map<String, Object> attributes;
	private String msg;
	private String sheet;

	public ImportResult(String sheet) {
		this.sheet = sheet;
	}

	public String getSheet() {
		return sheet;
	}

	public void setSheet(String sheet) {
		this.sheet = sheet;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getSucessRows() {
		if (this.sucessRows == null) {
			return null;
		}
		return sucessRows;
	}

	public void addSucessRow(T o) {
		if (this.getSuccess()) {
			if (this.sucessRows == null) {
				this.sucessRows = new ArrayList<T>();
			}
			this.sucessRows.add(o);
		}
	}

	public void setSucessRows(List<T> sucessRows) {
		this.sucessRows = sucessRows;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	public ImportResult<T> addError(int row, String column, String msg) {
		Error error = new Error(row, column, msg);
		if (errors == null) {
			errors = new ArrayList<Error>();
		}
		error.setSheet(this.getSheet());
		errors.add(error);
		this.setSuccess(Boolean.FALSE);
		return this;
	}

	public ImportResult<T> addError(int row, String msg) {
		return this.addError(row, null, msg);
	}

	public static <T> ImportResult<T> error(String sheet, String msg) {
		ImportResult<T> result = new ImportResult<T>(sheet);
		result.setMsg(msg);
		result.setSuccess(Boolean.FALSE);
		return result;
	}

	public static class Error {
		private String sheet;
		private int row;
		private String column;
		private String msg;

		public Error(int row, String column, String msg) {
			this.row = row;
			this.column = column;
			this.msg = msg;
		}

		public String getSheet() {
			return sheet;
		}

		public void setSheet(String sheet) {
			this.sheet = sheet;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public String getColumn() {
			return column;
		}

		public void setColumn(String column) {
			this.column = column;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}