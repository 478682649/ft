package com.guazi.ft.db.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.ResourceServlet;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.guazi.ft.db.DruidDataSourceDO;
import com.guazi.ft.db.DruidDataSourceUtil;
import com.guazi.ft.db.DynamicDataSource;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.*;

/**
 * 常量数据库配置
 *
 * @author shichunyang
 */
@Configuration
@PropertySource(value = "classpath:/application.yml")
public class ConsignDataSourceConfig {

	@Bean("consignDataSourceMaster")
	@ConfigurationProperties(prefix = "druid.consign.master")
	public DruidDataSourceDO consignDataSourceMaster() {
		return new DruidDataSourceDO();
	}

	@Bean("consignDataSourceSlave1")
	@ConfigurationProperties(prefix = "druid.consign.slave1")
	public DruidDataSourceDO consignDataSourceSlave1() {
		return new DruidDataSourceDO();
	}

	@Bean("consignDataSourceSlave2")
	@ConfigurationProperties(prefix = "druid.consign.slave2")
	public DruidDataSourceDO consignDataSourceSlave2() {
		return new DruidDataSourceDO();
	}

	@Bean("consignDataSource")
	public DataSource consignDataSource(
			@Qualifier("consignDataSourceMaster") DruidDataSourceDO consignDataSourceMaster,
			@Qualifier("consignDataSourceSlave1") DruidDataSourceDO consignDataSourceSlave1,
			@Qualifier("consignDataSourceSlave2") DruidDataSourceDO consignDataSourceSlave2
	) {

		DynamicDataSource dynamicDataSource = new DynamicDataSource();

		List<String> slaveDataSourceKeys = new ArrayList<>();
		slaveDataSourceKeys.add(DruidDataSourceUtil.SLAVE1);
		slaveDataSourceKeys.add(DruidDataSourceUtil.SLAVE2);
		dynamicDataSource.setSlaveKeys(slaveDataSourceKeys);

		DruidDataSource master = DruidDataSourceUtil.getDruidDataSource(consignDataSourceMaster.getUrl(), consignDataSourceMaster.getUsername(), consignDataSourceMaster.getPassword(), consignDataSourceMaster.getMinIdle(), consignDataSourceMaster.getInitialSize(), consignDataSourceMaster.getMaxActive());
		DruidDataSource slave1 = DruidDataSourceUtil.getDruidDataSource(consignDataSourceSlave1.getUrl(), consignDataSourceSlave1.getUsername(), consignDataSourceSlave1.getPassword(), consignDataSourceSlave1.getMinIdle(), consignDataSourceSlave1.getInitialSize(), consignDataSourceSlave1.getMaxActive());
		DruidDataSource slave2 = DruidDataSourceUtil.getDruidDataSource(consignDataSourceSlave2.getUrl(), consignDataSourceSlave2.getUsername(), consignDataSourceSlave2.getPassword(), consignDataSourceSlave2.getMinIdle(), consignDataSourceSlave2.getInitialSize(), consignDataSourceSlave2.getMaxActive());

		Map<Object, Object> targetDataSources = new HashMap<>(16);
		targetDataSources.put(DruidDataSourceUtil.MASTER, master);
		targetDataSources.put(DruidDataSourceUtil.SLAVE1, slave1);
		targetDataSources.put(DruidDataSourceUtil.SLAVE2, slave2);
		dynamicDataSource.setTargetDataSources(targetDataSources);

		// 设置默认数据源
		dynamicDataSource.setDefaultTargetDataSource(slave1);

		return dynamicDataSource;
	}

	@Bean("consignTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("consignDataSource") DataSource dataSource, @Qualifier("consignSqlSessionFactory") SqlSessionFactory consignSqlSessionFactory) {
		// System.out.println(consignSqlSessionFactory.getConfiguration().getEnvironment().getDataSource() == dataSource);
		return new FtDataSourceTransactionManager(dataSource);
	}

	@Bean("consignJdbcTemplate")
	public JdbcTemplate jdbcTemplate(@Qualifier("consignDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean("statViewServlet")
	public ServletRegistrationBean statViewServlet() {
		ServletRegistrationBean<StatViewServlet> statViewServlet = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
		Map<String, String> parameters = new HashMap<>(16);
		parameters.put(ResourceServlet.PARAM_NAME_USERNAME, "root");
		parameters.put(ResourceServlet.PARAM_NAME_PASSWORD, "naodian12300");
		statViewServlet.setInitParameters(parameters);
		return statViewServlet;
	}

	@Bean("webStatFilter")
	public FilterRegistrationBean webStatFilter() {
		FilterRegistrationBean<WebStatFilter> webStatFilter = new FilterRegistrationBean<>();
		webStatFilter.setFilter(new WebStatFilter());

		Map<String, String> parameters = new HashMap<>(16);
		parameters.put(WebStatFilter.PARAM_NAME_EXCLUSIONS, "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		webStatFilter.setInitParameters(parameters);

		webStatFilter.setUrlPatterns(Collections.singletonList("/*"));
		return webStatFilter;
	}

	@Bean
	public ServletRegistrationBean hystrixMetricsStreamServlet() {
		ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean = new ServletRegistrationBean<>(new HystrixMetricsStreamServlet());
		registrationBean.addUrlMappings("/actuator/hystrix.stream");
		return registrationBean;
	}
}
