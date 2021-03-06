package com.guazi.ft;

import com.guazi.ft.common.JsonUtil;
import com.guazi.ft.redis.base.HashOperationsCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HashOperationsCacheTest {

	@Resource(name = "hashOperationsCache")
	private HashOperationsCache hashOperationsCache;

	public static final String USER_MAP = "user_map";

	/**
	 * 添加键值
	 */
	@Test
	public void put() {
		hashOperationsCache.put(USER_MAP, "username", "13264232894");
		hashOperationsCache.put(USER_MAP, "password", "naodian12300");
	}

	/**
	 * 获取map的大小
	 */
	@Test
	public void size() {
		long length = hashOperationsCache.size(USER_MAP);
		System.out.println(length);
	}

	/**
	 * 获取map
	 */
	@Test
	public void entries() {
		Map<String, String> map = hashOperationsCache.entries(USER_MAP);
		System.out.println(JsonUtil.object2Json(map));
	}

	/**
	 * 判断key是否存在
	 */
	@Test
	public void hasKey() {
		boolean flag = hashOperationsCache.hasKey(USER_MAP, "username");
		System.out.println(flag);
	}

	/**
	 * 批量添加键值
	 */
	@Test
	public void putAll() {
		Map<String, String> userMap = new HashMap<>(16);
		userMap.put("username", "春阳");
		userMap.put("age", "28");
		userMap.put("hobby", "guitar");

		hashOperationsCache.putAll(USER_MAP, userMap);
	}
}
