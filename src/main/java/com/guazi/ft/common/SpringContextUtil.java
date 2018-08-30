package com.guazi.ft.common;

import org.springframework.context.ApplicationContext;

/**
 * SpringContextUtil
 *
 * @author shichunyang
 */
public class SpringContextUtil {

	/**
	 * spring上下文对象
	 */
	private volatile static ApplicationContext applicationContext;

	/**
	 * 获取spring上下文对象
	 *
	 * @return spring上下文对象
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 设置spring上下文对象
	 *
	 * @param applicationContext spring上下文对象
	 */
	public static void setApplicationContext(ApplicationContext applicationContext) {
		// double check
		if (SpringContextUtil.applicationContext == null) {
			synchronized (SpringContextUtil.class) {
				if (SpringContextUtil.applicationContext == null) {
					SpringContextUtil.applicationContext = applicationContext;
				}
			}
		}
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}
}
