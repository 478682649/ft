package com.guazi.ft.db.dbutil;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * QueryRunnerConfig
 *
 * @author shichunyang
 */
@Configuration
public class QueryRunnerConfig {

	@Bean
	public TxDataSourcePool txDataSourcePool(@Qualifier("consignDataSource") DataSource dataSource) {
		TxDataSourcePool txDataSourcePool = new TxDataSourcePool();
		txDataSourcePool.setDataSource(dataSource);
		return txDataSourcePool;
	}

	@Bean
	public TxQueryRunner txQueryRunner(TxDataSourcePool txDataSourcePool) {
		TxQueryRunner txQueryRunner = new TxQueryRunner();
		txQueryRunner.setTxDataSourcePool(txDataSourcePool);
		return txQueryRunner;
	}
}
