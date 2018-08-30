package com.guazi.ft.db.dbutil;

import lombok.Data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * TxDataSourcePool
 *
 * @author shichunyang
 */
@Data
public class TxDataSourcePool {
	/**
	 * 数据源
	 */
	private DataSource dataSource;

	/**
	 * 事务专用Connection
	 */
	private ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

	/**
	 * 获取Connection对象
	 * 如果有事务,返回当前事务的connection
	 * 如果没有事务,通过连接池返回新的connection
	 */

	public Connection getConnection() {

		// 获取事务连接
		Connection connection = connectionThreadLocal.get();

		// 若没有事务返回普通连接
		if (connection == null) {
			try {
				return dataSource.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("TxDataSourcePool 获取数据库连接失败");
			}
		}
		return connection;
	}

	/**
	 * 开启事务,获取一个Connection,并设置非自动提交。
	 */
	public void startTransaction() {

		// 获取当前线程的事务连接
		Connection connection = connectionThreadLocal.get();

		if (connection != null) {
			throw new RuntimeException("TxDataSourcePool 事务已经开启了,请不要重复开启");
		}

		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("TxDataSourcePool 获取数据库连接失败");
		}

		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("TxDataSourcePool 开启事务失败");
		}

		// 将事物连接保存在当前线程中
		connectionThreadLocal.set(connection);
	}

	/**
	 * 提交事务
	 */
	public void commitTransaction() {

		// 获取当前线程事务连接
		Connection connection = connectionThreadLocal.get();

		if (connection == null) {
			throw new RuntimeException("TxDataSourcePool 当前线程没有事务,不能提交");
		}

		try {
			connection.commit();
			connection.close();

			connectionThreadLocal.remove();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("TxDataSourcePool 提交事务失败");
		}
	}

	/**
	 * 回滚事务
	 */
	public void rollbackTransaction() {

		Connection connection = connectionThreadLocal.get();

		if (connection == null) {
			throw new RuntimeException("TxDataSourcePool 当前线程没有事务,不能回滚");
		}

		try {
			connection.rollback();
			connection.close();

			connectionThreadLocal.remove();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("TxDataSourcePool 回滚事务失败");
		}
	}

	/**
	 * 释放非事物连接
	 */
	public void releaseConnection(Connection connection) {

		// 获取当前线程的事务连接
		Connection transactionConnection = connectionThreadLocal.get();

		try {
			if (connection != null && connection != transactionConnection && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭三种资源
	 */
	public void close(ResultSet resultSet, Statement statement, Connection connection) {

		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
