package com.guazi.ft.db.mybatis;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * ConsignMyBatisConfig
 *
 * @author shichunyang
 */
@Configuration
@MapperScan(basePackages = {"com.guazi.ft.dao.consign"}, sqlSessionFactoryRef = "consignSqlSessionFactory")
public class ConsignMyBatisConfig {

    private final DataSource consignDataSource;

    @Autowired
    public ConsignMyBatisConfig(@Qualifier("consignDataSource") DataSource consignDataSource) {
        this.consignDataSource = consignDataSource;
    }

    @Bean("consignSqlSessionFactory")
    public SqlSessionFactory consignSqlSessionFactory() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(consignDataSource);
        // 用于主从分离
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new AutoSwitchDatasourceInterceptor()});

        try {
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
            MybatisUtil.initSqlSessionFactory(sqlSessionFactory);
            return sqlSessionFactory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("consignSqlSessionFactory fail");
        }
    }

    @Bean("consignSqlSessionTemplate")
    public SqlSessionTemplate consignSqlSessionTemplate(@Qualifier("consignSqlSessionFactory") SqlSessionFactory consignSqlSessionFactory) {
        return new SqlSessionTemplate(consignSqlSessionFactory);
    }
}