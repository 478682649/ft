package com.guazi.ft.db.dbutil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 带有事务的QueryRunner
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TxQueryRunner extends QueryRunner {

    private TxDataSourcePool txDataSourcePool;

    /**
     * 批处理
     */
    public int[] txBatch(String sql, Object[][] params) throws SQLException {

        Connection connection = txDataSourcePool.getConnection();

        int[] resultArr = super.batch(connection, sql, params);

        // 释放非事务连接
        txDataSourcePool.releaseConnection(connection);

        return resultArr;
    }

    /**
     * 查询
     */
    public <T> T txQuery(String sql, ResultSetHandler<T> resultSetHandler, Object... params) throws SQLException {

        Connection connection = txDataSourcePool.getConnection();

        T result = super.query(connection, sql, resultSetHandler, params);

        // 释放非事务连接
        txDataSourcePool.releaseConnection(connection);

        return result;
    }

    /**
     * 增删改
     */
    public int txUpdate(String sql, Object... params) throws SQLException {

        Connection connection = txDataSourcePool.getConnection();

        int result = super.update(connection, sql, params);

        // 释放非事务连接
        txDataSourcePool.releaseConnection(connection);

        return result;
    }
}
