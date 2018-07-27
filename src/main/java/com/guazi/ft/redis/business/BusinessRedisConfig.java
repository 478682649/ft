package com.guazi.ft.redis.business;

import com.guazi.ft.common.JsonUtil;
import com.guazi.ft.redis.RedisUtil;
import com.guazi.ft.redis.base.HashOperationsCache;
import com.guazi.ft.redis.base.ListOperationsCache;
import com.guazi.ft.redis.base.SetOperationsCache;
import com.guazi.ft.redis.base.ValueOperationsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * BusinessRedisConfig
 *
 * @author shichunyang
 */
@Configuration
@EnableConfigurationProperties({BusinessRedisDO.class})
public class BusinessRedisConfig {

    public static final String BUSINESS = "business";

    private final BusinessRedisDO businessRedisDO;

    @Autowired
    public BusinessRedisConfig(BusinessRedisDO businessRedisDO) {
        this.businessRedisDO = businessRedisDO;
    }

    @Bean(BUSINESS + "JedisPoolConfig")
    public JedisPoolConfig getJedisPoolConfig() {
        return RedisUtil.getJedisPoolConfig(businessRedisDO.getMinIdle(), businessRedisDO.getMaxIdle(), businessRedisDO.getMaxTotal());
    }

    @Bean(BUSINESS + "JedisConnectionFactory")
    public JedisConnectionFactory getJedisConnectionFactory(@Qualifier(BUSINESS + "JedisPoolConfig") JedisPoolConfig jedisPoolConfig) {
        return RedisUtil.getJedisConnectionFactory(jedisPoolConfig, businessRedisDO.getHostName(), businessRedisDO.getPort(), businessRedisDO.getPassword(), businessRedisDO.getDatabase());
    }

    @Bean
    public StringRedisSerializer getStringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public GenericJackson2JsonRedisSerializer getGenericJackson2JsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(new JsonUtil.JsonMapper());
    }

    @Bean(BUSINESS + "RedisTemplate")
    public RedisTemplate<String, String> getRedisTemplate(
            StringRedisSerializer stringRedisSerializer,
            GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer,
            @Qualifier(BUSINESS + "JedisConnectionFactory") JedisConnectionFactory jedisConnectionFactory

    ) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        return redisTemplate;
    }

    @Bean(BUSINESS + "ValueOperationsCache")
    public ValueOperationsCache getValueOperationsCache(@Qualifier(BUSINESS + "RedisTemplate") RedisTemplate<String, String> redisTemplate) {
        return new ValueOperationsCache(redisTemplate);
    }

    @Bean(BUSINESS + "ListOperationsCache")
    public ListOperationsCache getListOperationsCache(@Qualifier(BUSINESS + "RedisTemplate") RedisTemplate<String, String> redisTemplate) {
        return new ListOperationsCache(redisTemplate);
    }

    @Bean(BUSINESS + "SetOperationsCache")
    public SetOperationsCache getSetOperationsCache(@Qualifier(BUSINESS + "RedisTemplate") RedisTemplate<String, String> redisTemplate) {
        return new SetOperationsCache(redisTemplate);
    }

    @Bean(BUSINESS + "HashOperationsCache")
    public HashOperationsCache getHashOperationsCache(@Qualifier(BUSINESS + "RedisTemplate") RedisTemplate<String, String> redisTemplate) {
        return new HashOperationsCache(redisTemplate);
    }
}
