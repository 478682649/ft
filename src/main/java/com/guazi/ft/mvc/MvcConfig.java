package com.guazi.ft.mvc;

import com.guazi.ft.common.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.MultipartConfigElement;
import java.nio.charset.Charset;
import java.util.List;

/**
 * MvcConfig
 *
 * @author shichunyang
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 设置是否是后缀模式匹配
        configurer.setUseSuffixPatternMatch(false)
                // 设置是否自动后缀路径模式匹配
                .setUseTrailingSlashMatch(true);

    }

    /**
     * 对静态资源的配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String fe = "file:/data/web/";

        String webjars = "classpath:/META-INF/resources/webjars/";
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/", webjars, "classpath:/META-INF/resources/", fe);
        registry.addResourceHandler("/webjars/**").addResourceLocations(webjars);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        if (CollectionUtils.isEmpty(converters)) {
            return;
        }

        converters.forEach(converter -> {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                mappingJackson2HttpMessageConverter.setObjectMapper(new JsonUtil.JsonMapper());
            } else if (converter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter stringHttpMessageConverter = (StringHttpMessageConverter) converter;
                // 去除响应中服务器可接收编码
                stringHttpMessageConverter.setWriteAcceptCharset(false);
                stringHttpMessageConverter.setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
    }

    /**
     * 文件上传配置
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {

        MultipartConfigFactory factory = new MultipartConfigFactory();

        // 单个文件最大
        factory.setMaxFileSize("5MB");

        /// 设置总上传数据总大小
        factory.setMaxRequestSize("20MB");

        return factory.createMultipartConfig();
    }
}
