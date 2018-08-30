package com.guazi.ft.config.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.alibaba.dubbo.rpc.cluster.loadbalance.RoundRobinLoadBalance;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * DubboConfiguration
 *
 * @author shichunyang
 */
//@Configuration
@DubboComponentScan(basePackages = "com.guazi.ft.dubbo")
public class DubboConfiguration {

	public static final String CURATOR = "curator";

	@Bean("ft")
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName("ftApplication");
		return applicationConfig;
	}

	@Bean("ftRegistry")
	public RegistryConfig registryConfig() {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress("zookeeper://127.0.0.1:2181" + "?backup=" + "127.0.0.1:2181" + ",127.0.0.1:2181");
		registryConfig.setClient(CURATOR);
		registryConfig.setTimeout(30_000);
		registryConfig.setUsername("xxx");
		registryConfig.setPassword("xxx");

		Map<String, String> params = new HashMap<>(16);
		params.put("qos.enable", "false");
		registryConfig.setParameters(params);

		return registryConfig;
	}

	@Bean
	public ConsumerConfig consumerConfig() {
		ConsumerConfig consumerConfig = new ConsumerConfig();
		consumerConfig.setTimeout(30_000);
		consumerConfig.setRetries(3);
		consumerConfig.setLoadbalance(RoundRobinLoadBalance.NAME);
		consumerConfig.setCheck(true);
		return consumerConfig;
	}

	/**
	 * //@Reference(loadbalance = RoundRobinLoadBalance.NAME, version = "1.0.0", url = "dubbo://127.0.0.1:28081", group = "A")
	 */
	@Bean("ftProtocol")
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setPort(28081);
		protocolConfig.setThreads(200);
		protocolConfig.setName("dubbo");
		return protocolConfig;
	}
}
