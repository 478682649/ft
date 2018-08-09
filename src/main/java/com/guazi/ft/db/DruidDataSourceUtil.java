package com.guazi.ft.db;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.SQLException;

/**
 * druid 数据库连接池工具类
 *
 * @author shichunyang
 */
public class DruidDataSourceUtil {

    /**
     * 主库
     */
    public static final String MASTER = "master";
    /**
     * 从库1
     */
    public static final String SLAVE1 = "slave1";
    /**
     * 从库2
     */
    public static final String SLAVE2 = "slave2";

    /**
     * 获取德鲁伊连接池数据源
     *
     * @param url         数据库连接地址
     * @param username    数据库用户名
     * @param password    数据库密码
     * @param minIdle     最小连接池数量
     * @param initialSize 初始化时建立物理连接的个数
     * @param maxActive   最大连接池数量
     * @return 初始化后的德鲁伊连接池对象
     */
    public static DruidDataSource getDruidDataSource(
            String url,
            String username,
            String password,
            int minIdle,
            int initialSize,
            int maxActive
    ) {
        DruidDataSource druidDataSource = new DruidDataSource();

        // 数据库连接地址
        druidDataSource.setUrl(url);
        // 数据库用户名
        druidDataSource.setUsername(username);
        // 数据库密码
        druidDataSource.setPassword(password);
        // 最小连接池数量
        druidDataSource.setMinIdle(minIdle);
        // 初始化时建立物理连接的个数
        druidDataSource.setInitialSize(initialSize);
        // 最大连接池数量
        druidDataSource.setMaxActive(maxActive);

        try {
            init(druidDataSource);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("druid datasource init fail");
        }

        try {
            druidDataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("druid datasource init fail");
        }

        return druidDataSource;
    }

    /**
     * 对连接池进行初始化
     *
     * @param druidDataSource druid连接池对象
     */
    private static void init(DruidDataSource druidDataSource) throws Exception {
        // 扩展插件
        druidDataSource.setFilters("stat,wall,log4j");
        // 获取连接时最大等待时间(单位毫秒)
        druidDataSource.setMaxWait(10_000L);
        // 配置间隔多久才进行一次检测,检测需要关闭的空闲连接(单位是毫秒)
        druidDataSource.setTimeBetweenEvictionRunsMillis(300_000L);
        // 配置一个连接在池中最小生存的时间(单位是毫秒)
        druidDataSource.setMinEvictableIdleTimeMillis(300_000L);
        // 检测连接是否有效的sql
        druidDataSource.setValidationQuery("SELECT 'x'");
        // 建议配置为true,不影响性能,并且保证安全性。申请连接的时候检测,如果空闲时间大于timeBetweenEvictionRunsMillis,执行validationQuery检测连接是否有效。
        druidDataSource.setTestWhileIdle(true);
        // 申请连接时执行validationQuery检测连接是否有效,做了这个配置会降低性能。
        druidDataSource.setTestOnBorrow(true);
        // 归还连接时执行validationQuery检测连接是否有效,做了这个配置会降低性能。
        druidDataSource.setTestOnReturn(false);

        // 是否缓存preparedStatement
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(50);
    }
}
