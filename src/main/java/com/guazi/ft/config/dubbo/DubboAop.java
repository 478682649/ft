package com.guazi.ft.config.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;
import com.guazi.ft.aop.LogAspect;
import com.guazi.ft.common.CommonUtil;
import com.guazi.ft.common.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import static com.guazi.ft.aop.LogAspect.REQUEST_ID;

/**
 * DubboAop
 *
 * @author shichunyang
 */
@Component
@Aspect
@Slf4j
public class DubboAop {

    @Pointcut("execution(* com.guazi.ft.dubbo.service.impl.*.*(..))")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {

        String requestId = RpcContext.getContext().getAttachment(REQUEST_ID);
        if (StringUtil.isNull(requestId)) {
            requestId = CommonUtil.get32UUID();
        }

        MDC.put(REQUEST_ID, requestId);
        LogAspect.REQUEST_HEADER.set(requestId);
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void after(Object result) {
        MDC.remove(REQUEST_ID);
        LogAspect.REQUEST_HEADER.remove();
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void afterThrowing(Exception e) {
        MDC.remove(REQUEST_ID);
        LogAspect.REQUEST_HEADER.remove();
    }
}
