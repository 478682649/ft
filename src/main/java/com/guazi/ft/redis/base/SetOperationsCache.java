package com.guazi.ft.redis.base;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.Set;

/**
 * SetOperationsCache
 *
 * @author shichunyang
 */
public class SetOperationsCache extends BaseRedisTemplate {

	private SetOperations<String, String> operations;

	public SetOperationsCache(RedisTemplate<String, String> redisTemplate) {

		super.setRedisTemplate(redisTemplate);
		this.operations = redisTemplate.opsForSet();
	}

	/**
	 * 添加元素
	 */
	public long add(String key, String... values) {
		return operations.add(key, values);
	}

	/**
	 * 获取Set集合
	 */
	public Set<String> members(String key) {
		return operations.members(key);
	}

	/**
	 * 判断键知否存在
	 */

	public boolean isMember(String key, String value) {
		return operations.isMember(key, value);
	}

	/**
	 * 返回set的元素个数
	 */
	public long size(String key) {
		return operations.size(key);
	}

	/**
	 * 删除元素
	 */
	public long remove(String key, Object... values) {
		return operations.remove(key, values);
	}

	/**
	 * 随机出栈一个值
	 */
	public String pop(String key) {
		return operations.pop(key);
	}
}
