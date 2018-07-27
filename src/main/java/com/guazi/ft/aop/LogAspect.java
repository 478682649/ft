package com.guazi.ft.aop;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.guazi.ft.common.*;
import com.guazi.ft.dao.consign.model.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 日志切面
 *
 * @author shichunyang
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    public static final String REQUEST_ID = "request_id";
    public static final String USER_NAME = "user_name";

    private ThreadLocal<Long> startTime = new ThreadLocal<>();
    public static final TransmittableThreadLocal<String> REQUEST_HEADER = new TransmittableThreadLocal<>();

    @Pointcut("execution(* com.guazi.ft..controller.*.*(..))")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {

        // 获取登陆用户
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
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

        // 打印方法名称
        String method = joinPoint.getTarget().getClass().getName() +
                "." + joinPoint.getSignature().getName();

        // 获取被代理的对象
        Object target = joinPoint.getTarget();
        Class<?> targetClass = target.getClass();
        // 获取目标方法的名称
        String methodName = joinPoint.getSignature().getName();

        // 获取目标方法的参数
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        try {
            Method methodClass = targetClass.getMethod(methodName, parameterTypes);

            if (methodClass != null && methodClass.isAnnotationPresent(RequestMapping.class)) {

                RequestMapping requestMapping = methodClass.getAnnotation(RequestMapping.class);

                log.info("request controller==>{}, args==>{}, mapping==>{}", method, joinPoint.getArgs(), requestMapping.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void after(Object result) {

        // 获取request对象
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        String paramStr = CommonUtil.getParamsFromRequest(request);
        String ip = HttpUtil.getIpAddress(request);

        // 记录耗时
        log.info("response ip==>{}, url==>{}, args==>{}, cost==>{}ms, result==>{}", ip, request.getRequestURL(), paramStr, System.currentTimeMillis() - startTime.get(), result);

        startTime.remove();
        MDC.remove(REQUEST_ID);
        REQUEST_HEADER.remove();
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void afterThrowing(Exception e) {
        startTime.remove();
        REQUEST_HEADER.remove();
    }
}
