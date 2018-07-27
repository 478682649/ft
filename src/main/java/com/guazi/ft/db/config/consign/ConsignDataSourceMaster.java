package com.guazi.ft.db.config.consign;

import com.guazi.ft.db.DruidDataSourceDO;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * consign 主库
 *
 * @author shichunyang
 */
@ConfigurationProperties(prefix = "druid.consign.master")
public class ConsignDataSourceMaster extends DruidDataSourceDO {
}
