package com.swak.reactivex.web;

import java.io.Serializable;

import com.swak.exception.ErrorCode;
import com.swak.utils.JsonMapper;
import com.swak.utils.StringUtils;

/**
 * 前台和公用部分用户替代 AjaxResult
 * @author
 */
public class Result implements Serializable{
	
	private static final long serialVersionUID = 80931494242205860L;
	
	// 响应码
	private int code = ErrorCode.OPERATE_SECCESS.getCode();
	
	// 响应消息
	private String msg = "操作成功";
	
	// 返回的结果
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
		return (T)obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public boolean isSuccess() {
		return ErrorCode.OPERATE_SECCESS.getCode() == this.code;
	}
	
	/**
	 * 默认的成功消息
	 * @param o
	 * @return
	 */
	public static Result success(){
		return new Result();
	}
	
	/**
	 * 返回对象
	 * @param o
	 * @return
	 */
	public static Result success(Object o){
		Result result = Result.success();
		result.setObj(o);
		return result;
	}
	
	/**
	 * 指定错误消息、错误对象
	 * @param msg
	 * @param o
	 * @return
	 */
	public static Result error(String msg){
		Result result = new Result();
		result.setCode(ErrorCode.OPERATE_FAILURE.getCode());
		result.setMsg(msg);
		return result;
	}
	
	/**
	 * 指定错误消息、错误对象
	 * @param msg
	 * @param o
	 * @return
	 */
	public static Result error(String msg, Object o){
		Result result = new Result();
		result.setCode(ErrorCode.OPERATE_FAILURE.getCode());
		result.setMsg(msg);
		result.setObj(o);
		return result;
	}
	
	/**
	 * 格式化输出
	 * @param template
	 * @param msg
	 * @return
	 */
	public static Result error(String template, Object...msg){
		return error(StringUtils.format(template, msg));
	}
	
	/**
	 * 通过 ErrorCode 返回错误信息
	 * @param code
	 * @return
	 */
	public static Result error(ErrorCode code){
		Result result = new Result();
		result.setCode(code.getCode());
		result.setMsg(code.getMsg());
		result.setObj(code.getReason());
		return result;
	}
	
	/**
	 * 得到json格式的数据
	 * @return
	 */
	public String toJson() {
		return JsonMapper.toJson(this);
	}
}