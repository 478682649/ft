package com.guazi.ft.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Stream;

/**
 * Cookie 工具类
 *
 * @author shichunyang
 */
public class CookieUtil {

	/**
	 * Cookie存储在浏览器中
	 */
	public static final int MAX_AGE_BROWSER = -1;
	/**
	 * 删除Cookie
	 */
	public static final int MAX_AGE_DELETE = 0;

	/**
	 * 获取指定名称的cookie值
	 */
	public static String getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		Cookie cookie = Stream.of(cookies)
				.filter(c -> cookieName.equals(c.getName()))
				.findFirst()
				.orElse(null);

		return cookie == null ? null : cookie.getValue();
	}

	/**
	 * 设置cookie
	 *
	 * @param response 响应对象
	 * @param name     cookie名称
	 * @param value    cookie值
	 * @param maxAge   生命
	 * @param domain   cookie域
	 * @param httpOnly 是否禁止js捕获
	 */
	public static void addCookie(
			HttpServletResponse response,
			String name,
			String value,
			int maxAge,
			String domain,
			boolean httpOnly
	) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookie.setHttpOnly(httpOnly);

		response.addCookie(cookie);
	}
}
