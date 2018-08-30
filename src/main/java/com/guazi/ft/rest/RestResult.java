package com.guazi.ft.rest;

import com.guazi.ft.Swagger2;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * rest接口返回结果
 *
 * @author shichunyang
 */
@Data
public class RestResult<T> {

	public static final String API = "/api";
	public static final int SUCCESS_CODE = 0;
	public static final int ERROR_CODE = 500;
	/**
	 * 签名错误
	 */
	public static final int ERROR_SIGN_CODE = 401;

	/**
	 * 状态码
	 */
	@ApiModelProperty(value = "响应状态码", required = true, dataType = Swagger2.DATA_TYPE_INT, example = "0")
	private Integer code;

	/**
	 * 请求说明信息
	 */
	@ApiModelProperty(value = "响应说明信息", required = true, dataType = Swagger2.DATA_TYPE_STRING, example = "请求成功")
	private String message;

	/**
	 * 返回数据
	 */
	@ApiModelProperty(value = "响应数据", required = true)
	private T data;

	public RestResult(Integer code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> RestResult<T> getSuccessRestResult(T data) {
		return new RestResult<>(SUCCESS_CODE, "请求成功", data);
	}

	public static <T> RestResult<T> getErrorRestResult(String message, T data) {
		return new RestResult<>(ERROR_CODE, message, data);
	}

	private RestResult() {
	}
}
