package com.swak.entity;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import com.swak.annotation.ApiDoc;
import com.swak.exception.ErrorCode;
import com.swak.utils.JsonMapper;
import com.swak.utils.StringUtils;

/**
 * JSON 返回的基本结构
 *
 * @author: lifeng
 * @date: 2020/3/29 11:20
 */
public class Result implements Serializable {

	private static final long serialVersionUID = 1L;

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
	 * @return json 格式
	 */
	public String toJson() {
		return JsonMapper.toJson(this);
	}

	/**
	 * 确定的异步结果
	 * 
	 * @return
	 */
	public CompletableFuture<Result> toFuture() {
		return CompletableFuture.completedFuture(this);
	}

	/**
	 * 默认的成功消息
	 *
	 * @return Result
	 */
	public static Result success() {
		return new Result();
	}

	/**
	 * 返回对象
	 *
	 * @param o 数据
	 * @return Result
	 */
	public static Result success(Object o) {
		Result result = Result.success();
		result.setObj(o);
		return result;
	}

	/**
	 * 指定错误消息、错误对象
	 *
	 * @param msg 错误消息
	 * @return Result
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
	 * @param msg 错误消息
	 * @param o   错误的对象
	 * @return Result
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
	 * @param template 错误的模板
	 * @param msg      消息
	 * @return Result
	 */
	public static Result error(String template, Object... msg) {
		return error(StringUtils.format(template, msg));
	}

	/**
	 * 通过 ErrorCode 返回错误信息
	 *
	 * @param code 错误码
	 * @return Result
	 */
	public static Result error(ErrorCode code) {
		Result result = new Result();
		result.setCode(code.getCode());
		result.setMsg(code.getMsg());
		result.setObj(code.getReason());
		return result;
	}
}