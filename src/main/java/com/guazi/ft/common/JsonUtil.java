package com.guazi.ft.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

/**
 * JSON处理工具类
 *
 * @author shichunyang
 */
public class JsonUtil {

	private JsonUtil() {
	}

	/**
	 * 将JSON格式数据转换成对象(失败返回NULL)
	 *
	 * @param json  json字符串
	 * @param clazz java类 类型
	 * @param <T>   泛型
	 * @return java对象
	 */
	public static <T> T json2Object(final String json, final Class<T> clazz) {
		try {
			JsonMapper mapper = new JsonMapper();

			return mapper.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T json2Object(String json, TypeReference<T> typeReference) {
		try {
			JsonMapper mapper = new JsonMapper();
			return mapper.readValue(json, typeReference);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将JAVA对象转换成JSON字符串(失败返回NULL)
	 *
	 * @param object java对象
	 * @return json字符串
	 */
	public static String object2Json(final Object object) {
		JsonMapper mapper = new JsonMapper();

		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static class JsonMapper extends ObjectMapper {
		private static final long serialVersionUID = 1L;

		static ObjectMapper mapper;

		static {
			mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			// 2.9之后版本过时
			// mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
			mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		}

		public JsonMapper() {
			super(mapper);
		}
	}
}
