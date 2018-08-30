package com.guazi.ft.exception;

import com.guazi.ft.rest.RestResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FtException extends RuntimeException {

	private static final long serialVersionUID = -4630272752707164770L;

	private Integer code;

	private String message;

	public FtException(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public FtException(String message, Object... args) {
		this.code = RestResult.ERROR_CODE;
		this.message = String.format(message, args);
	}

}
