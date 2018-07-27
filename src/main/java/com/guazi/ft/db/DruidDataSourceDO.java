package com.guazi.ft.db;

import lombok.Data;

/**
 * druid 模型类
 *
 * @author shichunyang
 */
@Data
public class DruidDataSourceDO {
    /**
     * 数据库连接地址
     */
    private String url;
    /**
     * 数据库用户名
     */
    private String username;
    /**
     * 数据库密码
     */
    private String password;
    /**
     * 最小连接池数量
     */
    private Integer minIdle;
    /**
     * 初始化时建立物理连接的个数
     */
    private Integer initialSize;
    /**
     * 最大连接池数量
     */
    private Integer maxActive;

    public void setUrl(String url) {
        this.url = (url
                + "?useUnicode=true"
                + "&characterEncoding=utf8"
                + "&useServerPrepStmts=true"
                + "&cachePrepStmts=true"
                + "&rewriteBatchedStatements=true"
                + "&prepStmtCacheSize=256"
                + "&prepStmtCacheSqlLimit=2048"
                + "&useSSL=false"
        );
    }
}
