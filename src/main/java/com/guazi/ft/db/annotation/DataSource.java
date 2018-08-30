package com.guazi.ft.db.annotation;

import com.guazi.ft.db.DruidDataSourceUtil;

import java.lang.annotation.*;

/**
 * 读写分离数据源注解
 *
 * @author shichunyang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
	/**
	 * 可读写数据库
	 */
	String master = DruidDataSourceUtil.MASTER;

	/**
	 * 只读数据库
	 */
	String slave = "slave";

	String value() default master;
}