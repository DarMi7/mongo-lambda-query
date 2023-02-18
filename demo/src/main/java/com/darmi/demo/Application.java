package com.darmi.demo;

import com.darmi.plugin.EnableMongoLambdaQuery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author darmi
 */
@SpringBootApplication(exclude= DataSourceAutoConfiguration.class)
@EnableMongoLambdaQuery
@EnableMongoRepositories(basePackages = "com.darmi.demo.repository.mongo")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
