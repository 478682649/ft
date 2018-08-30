package com.guazi.ft;

import com.guazi.ft.common.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatisGenerator
 *
 * @author shichunyang
 */
@Slf4j
public class MyBatisGenerator {
	public static void main(String[] args) throws Exception {
		List<String> warnings = new ArrayList<>();

		File inputFile = new File(MyBatisGenerator.class.getResource("/mybatis/generatorConfig.xml").toURI());

		ConfigurationParser configurationParser = new ConfigurationParser(warnings);

		Configuration configuration = configurationParser.parseConfiguration(inputFile);

		DefaultShellCallback defaultShellCallback = new DefaultShellCallback(true);

		org.mybatis.generator.api.MyBatisGenerator myBatisGenerator = new org.mybatis.generator.api.MyBatisGenerator(configuration, defaultShellCallback, warnings);
		myBatisGenerator.generate(null);
		log.info(JsonUtil.object2Json(warnings));
	}
}
