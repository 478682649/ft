package com.guazi.ft.db.config.consign;

import com.guazi.ft.db.DruidDataSourceDO;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * consign 从库1
 *
 * @author shichunyang
 */
@ConfigurationProperties(prefix = "druid.consign.slave1")
public class ConsignDataSourceSlave1 extends DruidDataSourceDO {
}
