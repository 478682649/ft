package com.guazi.ft.aop;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Lists;
import com.guazi.ft.common.*;
import com.guazi.ft.common.annotation.ConfigParam;
import com.guazi.ft.common.annotation.ConfigParams;
import com.guazi.ft.dao.consign.model.UserDO;
import com.guazi.ft.exception.FtException;
import com.guazi.ft.rest.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller切面、异常处理
 *
 * @author shichunyang
 */
@Order(1)
@Aspect
@Slf4j
public class ControllerAspect {

	public static final String REQUEST_ID = "request_id";
	public static final String USER_NAME = "user_name";

	private ThreadLocal<Long> startTime = new ThreadLocal<>();
	public static final TransmittableThreadLocal<String> REQUEST_HEADER = new TransmittableThreadLocal<>();

	private ExceptionHandler exceptionHandler;

	public ControllerAspect() {
		this.exceptionHandler = (request, response, throwable) -> null;
	}

	public ControllerAspect(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * Controller切点
	 */
	@Pointcut(
			"@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
					"@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
					"@annotation(org.springframework.web.bind.annotation.PostMapping) "
	)
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) {
		// 得到被代理的方法
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		Class<?> beanType = method.getDeclaringClass();

		Map<String, String> configParams = this.getConfigParams(beanType, method);

		ServletRequestAttributes attributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		HttpServletResponse response = attributes.getResponse();

		try {
			String result;

			this.init(request);

			Object[] params = joinPoint.getArgs();

			log.info("request controller==>{}, args==>{}", method, params);

			Object obj = joinPoint.proceed();
			if (obj instanceof String) {
				result = (String) obj;
			} else {
				result = JsonUtil.object2Json(obj);
			}

			String ip = HttpUtil.getIpAddress(request);
			// 记录耗时
			log.info("response ip==>{}, url==>{}, args==>{}, cost==>{}ms, result==>{}", ip, request.getRequestURL(), params, System.currentTimeMillis() - startTime.get(), result);

			startTime.remove();
			MDC.remove(REQUEST_ID);
			MDC.remove(USER_NAME);
			REQUEST_HEADER.remove();

			return obj;
		} catch (Throwable throwable) {
			startTime.remove();
			REQUEST_HEADER.remove();
			return exceptionHandler.handle(request, response, throwable);
		}
	}

	@FunctionalInterface
	public interface ExceptionHandler {
		/**
		 * 异常处理
		 *
		 * @param request   请求对象
		 * @param response  响应对象
		 * @param throwable 抛出异常
		 * @return 返回结果
		 */
		Object handlerException(
				HttpServletRequest request,
				HttpServletResponse response,
				Throwable throwable
		);

		/**
		 * 处理异常
		 *
		 * @param request   请求对象
		 * @param response  响应对象
		 * @param throwable 异常对象
		 * @return 返回结果
		 */
		default Object handle(
				HttpServletRequest request,
				HttpServletResponse response,
				Throwable throwable
		) {
			Object result = handlerException(request, response, throwable);
			if (result != null) {
				return result;
			}

			String paramStr = CommonUtil.getParamsFromRequest(request);
			String ip = HttpUtil.getIpAddress(request);

			RestResult<Object> restResult;
			if (throwable instanceof FtException) {
				log.info("ip==>{}, url==>{}, args==>{}, custom exception==>{}", ip, request.getRequestURL(), paramStr, throwable.getMessage());
				restResult = new RestResult<>(((FtException) throwable).getCode(), throwable.getMessage(), null);
			} else if (throwable instanceof MissingServletRequestParameterException) {
				MissingServletRequestParameterException missingServletRequestParameterException = (MissingServletRequestParameterException) throwable;
				restResult = new RestResult<>(RestResult.ERROR_CODE, "参数:" + missingServletRequestParameterException.getParameterName() + "必传, 参数类型:" + missingServletRequestParameterException.getParameterType(), null);
			} else if (throwable instanceof HttpRequestMethodNotSupportedException) {
				HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException = (HttpRequestMethodNotSupportedException) throwable;
				restResult = new RestResult<>(RestResult.ERROR_CODE, httpRequestMethodNotSupportedException.getMethod() + "请求不支持", null);
			} else if (throwable instanceof MethodArgumentNotValidException) {
				MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) throwable;
				restResult = new RestResult<>(RestResult.ERROR_CODE, methodArgumentNotValidException.getBindingResult().getFieldErrors().get(0).getDefaultMessage(), null);
			} else if (throwable instanceof HttpMessageNotReadableException) {
				restResult = new RestResult<>(RestResult.ERROR_CODE, throwable.getMessage(), null);
			} else if (throwable instanceof IOException) {
				restResult = new RestResult<>(RestResult.ERROR_CODE, throwable.getMessage(), null);
			} else {
				log.error("ip==>{}, url==>{}, args==>{}, exception==>{}", ip, request.getRequestURL(), paramStr, JsonUtil.object2Json(throwable), throwable);
				restResult = new RestResult<>(RestResult.ERROR_CODE, throwable.getMessage(), null);
			}

			MDC.remove(REQUEST_ID);
			MDC.remove(USER_NAME);

			return JsonUtil.object2Json(restResult);
		}
	}

	private void init(HttpServletRequest request) {
		String token = LoginUtil.getToken(request);
		if (!StringUtil.isNull(token)) {
			UserDO userDO = LoginUtil.getLoginUser(request);
			MDC.put(USER_NAME, userDO.getUsername());
		}

		String requestId = request.getHeader(REQUEST_ID);
		if (StringUtil.isNull(requestId)) {
			requestId = CommonUtil.get32UUID();
		}

		MDC.put(REQUEST_ID, requestId);
		REQUEST_HEADER.set(requestId);

		// 记录请求开始时间
		startTime.set(System.currentTimeMillis());
	}

	/**
	 * 获取配置参数
	 */
	private Map<String, String> getConfigParams(Class<?> beanType, Method method) {
		List<ConfigParam> configParams = Lists.newLinkedList();

		boolean annotationPresent = CommonUtil.isAnnotationPresent(beanType, null, ConfigParams.class);
		if (annotationPresent) {
			ConfigParam[] params = beanType.getAnnotation(ConfigParams.class).value();
			configParams.addAll(Arrays.asList(params));
		}

		annotationPresent = CommonUtil.isAnnotationPresent(null, method, ConfigParams.class);
		if (annotationPresent) {
			ConfigParam[] params = method.getAnnotation(ConfigParams.class).value();
			configParams.addAll(Arrays.asList(params));
		}

		return configParams.stream().collect(Collectors.toMap(ConfigParam::paramName, ConfigParam::paramValue));
	}
}
