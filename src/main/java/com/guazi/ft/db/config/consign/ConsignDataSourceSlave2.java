package com.guazi.ft.db.config.consign;

import com.guazi.ft.db.DruidDataSourceDO;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * consign 从库2
 *
 * @author shichunyang
 */
@ConfigurationProperties(prefix = "druid.consign.slave2")
public class ConsignDataSourceSlave2 extends DruidDataSourceDO {
}
