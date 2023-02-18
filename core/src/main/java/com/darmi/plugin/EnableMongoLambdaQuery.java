package com.darmi.plugin;

import com.darmi.plugin.spring.MongoLambdaQueryConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author darmi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MongoLambdaQueryConfig.class)
@Documented
public @interface EnableMongoLambdaQuery {
}
