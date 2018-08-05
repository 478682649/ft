package com.guazi.ft.db.aop;

import com.guazi.ft.common.JsonUtil;
import com.guazi.ft.db.annotation.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * 数据库动态数据源切换
 *
 * @author shichunyang
 */
@Order(-1000)
@Aspect
@Slf4j
public class DataSourceAspect {

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("@annotation(com.guazi.ft.db.annotation.DataSource)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {

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

            if (methodClass != null && methodClass.isAnnotationPresent(DataSource.class)) {

                DataSource dataSource = methodClass.getAnnotation(DataSource.class);
                String dynamic = dataSource.value();

                Object[] params = joinPoint.getArgs();
                log.info("service==>{}, args==>{}, 选择动态数据源==>{}", method, params, dynamic);

                DataSourceHolder.setDataSourceKey(dynamic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("动态获取数据源失败");
        }
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void after(JoinPoint joinPoint, Object result) {

        // 打印方法名称
        String method = joinPoint.getTarget().getClass().getName() +
                "." + joinPoint.getSignature().getName();

        Object[] params = joinPoint.getArgs();
        String str;
        if (result instanceof String) {
            str = (String) result;
        } else {
            str = JsonUtil.object2Json(result);
        }
        log.info("service==>{}, args==>{}, cost==>{}ms, result==>{}", method, params, System.currentTimeMillis() - startTime.get(), str);

        startTime.remove();
        DataSourceHolder.clearDataSourceKey();
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void afterThrowing(Exception e) {
        startTime.remove();
        DataSourceHolder.clearDataSourceKey();
    }
}
