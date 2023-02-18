package com.darmi.plugin.spring;


import org.springframework.context.annotation.Bean;

/**
 * @author darmi
 */
public class MongoLambdaQueryConfig {
    @Bean
    public SpringUtil springUtil() {
        return new SpringUtil();
    }
}
