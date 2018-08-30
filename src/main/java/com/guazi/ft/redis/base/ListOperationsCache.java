package com.guazi.ft.redis.base;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * ListOperationsCache
 *
 * @author shichunyang
 */
public class ListOperationsCache extends BaseRedisTemplate {

	private ListOperations<String, String> listOperations;

	public ListOperationsCache(RedisTemplate<String, String> redisTemplate) {

		super.setRedisTemplate(redisTemplate);
		this.listOperations = redisTemplate.opsForList();
	}

	/**
	 * 从头部依次插入
	 */
	public long leftPushAll(String key, String... values) {
		return listOperations.leftPushAll(key, values);
	}

	/**
	 * 从左到右遍历list
	 */
	public List<String> range(String key, long start, long end) {
		return listOperations.range(key, start, end);
	}

	/**
	 * 从尾部依次插入
	 */
	public long rightPushAll(String key, String... values) {
		return listOperations.rightPushAll(key, values);
	}

	/**
	 * 移除并返回头元素
	 */
	public String leftPop(String key) {
		return listOperations.leftPop(key);
	}

	/**
	 * 移除并返回尾元素
	 */
	public String rightPop(String key) {
		return listOperations.rightPop(key);
	}

	/**
	 * 返回list的长度
	 */
	public long size(String key) {
		return listOperations.size(key);
	}

	/**
	 * count==0时,移除list中所有指定值
	 */
	public long remove(String key, long count, String value) {
		return listOperations.remove(key, count, value);
	}

	/**
	 * 将指定范围的值,截取并赋值给指定的key(留头也留尾)
	 */
	public void trim(String key, long start, long end) {
		listOperations.trim(key, start, end);
	}
}
