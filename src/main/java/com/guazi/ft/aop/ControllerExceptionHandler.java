package com.guazi.ft.aop;

import com.guazi.ft.common.CommonUtil;
import com.guazi.ft.common.HttpUtil;
import com.guazi.ft.common.JsonUtil;
import com.guazi.ft.exception.FtException;
import com.guazi.ft.rest.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理Controller异常
 *
 * @author shichunyang
 */
@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public String runtimeExceptionHandler(Exception e) {
        // 获取request对象
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        String paramStr = CommonUtil.getParamsFromRequest(request);
        String ip = HttpUtil.getIpAddress(request);

        RestResult<Object> restResult;
        if (e instanceof FtException) {
            log.error("ip==>{}, url==>{}, args==>{}, custom exception==>{}", ip, request.getRequestURL(), paramStr, e.getMessage());
            restResult = new RestResult<>(((FtException) e).getCode(), e.getMessage(), null);
        } else if (e instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException missingServletRequestParameterException = (MissingServletRequestParameterException) e;
            restResult = new RestResult<>(RestResult.ERROR_CODE, "参数:" + missingServletRequestParameterException.getParameterName() + "必传, 参数类型:" + missingServletRequestParameterException.getParameterType(), null);
        } else {
            log.error("ip==>{}, url==>{}, args==>{}, exception==>{}", ip, request.getRequestURL(), paramStr, JsonUtil.object2Json(e), e);
            restResult = new RestResult<>(RestResult.ERROR_CODE, e.getMessage(), null);
        }

        MDC.remove(LogAspect.REQUEST_ID);

        return JsonUtil.object2Json(restResult);
    }
}
