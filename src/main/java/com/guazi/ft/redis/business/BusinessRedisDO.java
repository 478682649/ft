package com.guazi.ft.redis.business;

import com.guazi.ft.redis.RedisDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.guazi.ft.redis.business.BusinessRedisConfig.BUSINESS;

/**
 * BusinessRedisDO
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "redis." + BUSINESS)
@Data
public class BusinessRedisDO extends RedisDO {
}
