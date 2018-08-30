package com.guazi.ft.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @author shichunyang
 */
public class RegexUtil {
	/**
	 * url参数正则
	 */
	private static final String URL_REGEX = "[a-zA-Z][0-9]+";

	/**
	 * email正则
	 */
	public static final String EMAIL = "\\w+@\\w+(\\.[a-z]{2,3})+";

	/**
	 * 11位手机号正则
	 */
	public static final String CELL_PHONE = "(\\d{3})\\d{4}(\\d{4})";

	public static Map<String, String> urlParamSplit(String param) {
		if (param == null || param.trim().isEmpty()) {
			return null;
		}

		Map<String, String> parameterMap = new LinkedHashMap<>();

		Pattern pattern = Pattern.compile(URL_REGEX);
		Matcher match = pattern.matcher(param);
		while (match.find()) {

			String keyValue = match.group();

			String key = keyValue.substring(0, 1);
			String value = keyValue.substring(1);

			String mapValue = parameterMap.get(key);
			if (mapValue == null) {
				parameterMap.put(key, value);
			} else {
				parameterMap.put(key, mapValue + keyValue);
			}
		}

		return parameterMap;
	}

	/**
	 * 隐藏手机号
	 *
	 * @param cellPhone 原始手机号
	 * @return 隐藏后的手机号
	 */
	public static String hideCellPhone(String cellPhone) {
		return cellPhone.replaceAll(CELL_PHONE, "$1****$2");
	}

	public static void main(String[] args) {
		System.out.println(urlParamSplit("24a1b1c2d3d4d5d6h1h2i3jk"));
	}
}
