package com.guazi.ft.db.mybatis;

import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * MybatisUtil
 *
 * @author shichunyang
 */
public class MybatisUtil {

	public static void initSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {

		// 开启驼峰映射
		sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
		// 开启缓存
		sqlSessionFactory.getConfiguration().setCacheEnabled(true);
		// 启动延迟加载
		sqlSessionFactory.getConfiguration().setLazyLoadingEnabled(true);
		// 配合延迟加载,设置为false
		sqlSessionFactory.getConfiguration().setAggressiveLazyLoading(false);
		// 允许sql返回不同的结果集
		sqlSessionFactory.getConfiguration().setMultipleResultSetsEnabled(true);
		// 允许使用别名
		sqlSessionFactory.getConfiguration().setUseColumnLabel(true);
		// 允许自定义主键
		sqlSessionFactory.getConfiguration().setUseGeneratedKeys(true);
		// 允许嵌套映射
		sqlSessionFactory.getConfiguration().setAutoMappingBehavior(AutoMappingBehavior.FULL);
		// 配置默认执行器
		sqlSessionFactory.getConfiguration().setDefaultExecutorType(ExecutorType.SIMPLE);
		// 超时时间
		sqlSessionFactory.getConfiguration().setDefaultStatementTimeout(36000);
	}
}
