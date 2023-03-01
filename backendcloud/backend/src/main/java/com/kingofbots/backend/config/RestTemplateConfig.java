package com.kingofbots.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();     // 要用到xx,1 配置configuration 2 @Bean 3 返回一个xx对象实例
    }
}
