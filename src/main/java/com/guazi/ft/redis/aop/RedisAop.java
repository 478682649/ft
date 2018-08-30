package com.guazi.ft.redis.aop;

import com.guazi.ft.common.JsonUtil;
import com.guazi.ft.redis.annotation.RedisCache;
import com.guazi.ft.redis.base.ValueOperationsCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * RedisAop
 *
 * @author shichunyang
 */
@Aspect
@Slf4j
@SuppressWarnings("unchecked")
public class RedisAop {

	@Autowired
	private ValueOperationsCache valueOperationsCache;

	@Around(value = "@annotation(com.guazi.ft.redis.annotation.RedisCache)")
	public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {

		Object result;

		// 得到被代理的方法
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

		if (method != null && method.isAnnotationPresent(RedisCache.class)) {

			// 得到被代理的方法上的注解
			RedisCache annotation = method.getAnnotation(RedisCache.class);
			String redisKey = annotation.key() + ":" + Arrays.toString(joinPoint.getArgs());

			long timeout = annotation.timeout();

			String jsonValue = valueOperationsCache.get(redisKey);

			// 得到被代理方法的返回值类型
			Class returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();

			if (jsonValue == null) {

				// 缓存未命中
				result = joinPoint.proceed(joinPoint.getArgs());
				if (result == null) {
					return null;
				}

				jsonValue = JsonUtil.object2Json(result);

				if (timeout > 0) {
					valueOperationsCache.setNX(redisKey, jsonValue, timeout);
				} else {
					valueOperationsCache.setNX(redisKey, jsonValue);
				}
			} else {
				// 缓存命中
				result = JsonUtil.json2Object(jsonValue, returnType);
			}
		} else {
			result = joinPoint.proceed(joinPoint.getArgs());
		}

		return result;
	}
}
