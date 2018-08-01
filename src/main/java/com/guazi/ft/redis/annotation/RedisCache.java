package com.guazi.ft.redis.annotation;

import java.lang.annotation.*;

/**
 * RedisCache
 *
 * @author shichunyang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisCache {
    /**
     * 缓存数据Key
     *
     * @return key
     */
    String key() default "";

    /**
     * 缓存数据有效期, -1表示永久有效
     *
     * @return 过期时间
     */
    long timeout() default 300_000;
}
