package com.guazi.ft.aop.login;

import java.lang.annotation.*;

/**
 * 登录校验标志
 *
 * @author shichunyang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LoginCheck {
}
