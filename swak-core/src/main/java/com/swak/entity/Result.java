package com.swak.entity;

import java.io.Serializable;

import com.swak.annotation.ApiDoc;
import com.swak.exception.ErrorCode;
import com.swak.utils.JsonMapper;
import com.swak.utils.StringUtils;

/**
 * JSON 返回的基本结构
 * 
 * @ClassName:  Result   
 * @Description:TODO(描述这个类的作用)   
 * @author: lifeng
 * @date:   Nov 15, 2019 9:32:02 AM
 */
public class Result implements Serializable {

	private static final long serialVersionUID = 80931494242205860L;

	/**
	 * 响应码
	 */
	@ApiDoc("响应码：具体参考：ErrorCode")
	private int code = ErrorCode.OPERATE_SECCESS.getCode();

	/**
	 * 响应消息
	 */
	@ApiDoc("响应消息：操作成功/操作失败：显示操作失败消息")
	private String msg = "操作成功";
	
	/**
	 * 响应结果
	 */
	@ApiDoc("响应结果：true/false")
	private boolean success;

	/**
	 * 返回的结果
	 */
	@ApiDoc("响应结果：具体的响应数据")
	private Object obj = null;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@SuppressWarnings("unchecked")
	public <T> T getObj() {
		return (T) obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	/**
	 * 是否响应成功
	 */
	public boolean isSuccess() {
		return ErrorCode.OPERATE_SECCESS.getCode() == this.code;
	}

	/**
	 * 得到json格式的数据
	 * 
	 * @return
	 */
	public String toJson() {
		return JsonMapper.toJson(this);
	}

	/**
	 * 默认的成功消息
	 * 
	 * @param o
	 * @return
	 */
	public static Result success() {
		return new Result();
	}

	/**
	 * 返回对象
	 * 
	 * @param o
	 * @return
	 */
	public static Result success(Object o) {
		Result result = Result.success();
		result.setObj(o);
		return result;
	}

	/**
	 * 指定错误消息、错误对象
	 * 
	 * @param msg
	 * @param o
	 * @return
	 */
	public static Result error(String msg) {
		Result result = new Result();
		result.setCode(ErrorCode.OPERATE_FAILURE.getCode());
		result.setMsg(msg);
		return result;
	}

	/**
	 * 指定错误消息、错误对象
	 * 
	 * @param msg
	 * @param o
	 * @return
	 */
	public static Result error(String msg, Object o) {
		Result result = new Result();
		result.setCode(ErrorCode.OPERATE_FAILURE.getCode());
		result.setMsg(msg);
		result.setObj(o);
		return result;
	}

	/**
	 * 格式化输出
	 * 
	 * @param template
	 * @param msg
	 * @return
	 */
	public static Result error(String template, Object... msg) {
		return error(StringUtils.format(template, msg));
	}

	/**
	 * 通过 ErrorCode 返回错误信息
	 * 
	 * @param code
	 * @return
	 */
	public static Result error(ErrorCode code) {
		Result result = new Result();
		result.setCode(code.getCode());
		result.setMsg(code.getMsg());
		result.setObj(code.getReason());
		return result;
	}
}