package com.guazi.ft.db.config;

import com.guazi.ft.db.aop.DataSourceHolder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import javax.sql.DataSource;

/**
 * 带AOP的事务管理类
 *
 * @author shichunyang
 */
public class FtDataSourceTransactionManager extends DataSourceTransactionManager {

    public FtDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        // 事务环境下切换至主库
        DataSourceHolder.setDataSourceKey(com.guazi.ft.db.annotation.DataSource.master);
        super.doBegin(transaction, definition);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DataSourceHolder.clearDataSourceKey();
    }
}
