package com.guazi.ft.redis.base;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * HashOperationsCache
 *
 * @author shichunyang
 */
public class HashOperationsCache extends BaseRedisTemplate {

    private HashOperations<String, String, String> hashOperations;

    public HashOperationsCache(RedisTemplate<String, String> redisTemplate) {

        super.setRedisTemplate(redisTemplate);
        hashOperations = redisTemplate.opsForHash();
    }

    /**
     * 根据key获取Map
     */
    public Map<String, String> entries(String key) {
        return hashOperations.entries(key);
    }

    /**
     * 获取Map的长度
     */
    public long size(String key) {
        return hashOperations.size(key);
    }

    /**
     * 添加键值对
     */
    public void put(String key, String hashKey, String value) {
        hashOperations.put(key, hashKey, value);
    }

    /**
     * 添加所有键值对
     */
    public void putAll(String key, Map<String, String> map) {
        hashOperations.putAll(key, map);
    }

    /**
     * 判断某个key是否存在
     */
    public boolean hasKey(String key, String hashKey) {
        return hashOperations.hasKey(key, hashKey);
    }
}
