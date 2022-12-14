package com.darmi.plugin;

import com.darmi.plugin.spring.MongoLambdaQueryConfig;
import org.springframework.context.annotation.Import;

/**
 * @author darmi
 */
@Import(value = {MongoLambdaQueryConfig.class})
public @interface EnableMongoLambdaQuery {
}
