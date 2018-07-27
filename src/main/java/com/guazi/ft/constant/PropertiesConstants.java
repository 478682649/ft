package com.guazi.ft.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 系统常量配置
 *
 * @author shichunyang
 */
@Data
@ConfigurationProperties(prefix = "com.guazi")
public class PropertiesConstants {

    private String constant;
}
