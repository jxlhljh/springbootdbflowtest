package cn.gzsendi.modules.framework.model;

import cn.gzsendi.modules.framework.constants.RespCodeConstant;

/**
 * 接口返回格式
 * @author liujh
 * @param <T>
 */
public class Result<T> {

	private boolean success = true;
	private String message = "操作成功！";
	private Integer code = 200;
	private long timestamp = System.currentTimeMillis();
	private T data;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Result() {
		
	}
	
	public Result<T> error500(String message) {
		this.message = message;
		this.code = RespCodeConstant.INTERNAL_SERVER_ERROR_500;
		this.success = false;
		return this;
	}
	
	public Result<T> success(String message) {
		this.message = message;
		this.code = RespCodeConstant.OK_200;
		this.success = true;
		return this;
	}
	
	
	public static Result<Object> ok() {
		Result<Object> r = new Result<Object>();
		r.setSuccess(true);
		r.setCode(RespCodeConstant.OK_200);
		r.setMessage("成功");
		return r;
	}
	
	public static Result<Object> ok(String msg) {
		Result<Object> r = new Result<Object>();
		r.setSuccess(true);
		r.setCode(RespCodeConstant.OK_200);
		r.setMessage(msg);
		return r;
	}
	
	public static Result<Object> ok(Object data) {
		Result<Object> r = new Result<Object>();
		r.setSuccess(true);
		r.setCode(RespCodeConstant.OK_200);
		r.setData(data);
		return r;
	}
	
	public static Result<Object> error(String msg) {
		return error(RespCodeConstant.INTERNAL_SERVER_ERROR_500, msg);
	}

	public static Result<Object> error(Object data,String message) {
		Result<Object> r = new Result<Object>();
		r.setSuccess(false);
		r.setCode(RespCodeConstant.INTERNAL_SERVER_ERROR_500);
		r.setData(data);
		r.setMessage(message);
		return r;
	}

	public static Result<Object> error(int code, String msg) {
		Result<Object> r = new Result<Object>();
		r.setCode(code);
		r.setMessage(msg);
		r.setSuccess(false);
		return r;
	}
}
