package com.darmi.plugin.core;

import com.darmi.plugin.spring.SpringUtil;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author darmi
 */
@SuppressWarnings("serial")
public class MongoLambdaQuery<T> extends MongoAbstractLambdaQuery<T, MongoLambdaQuery<T>> {


    public static <T> MongoLambdaQuery<T> lambdaQuery(Class<T> entityClass) {
        return new MongoLambdaQuery<>(entityClass, SpringUtil.getBean(MongoTemplate.class));
    }

    public MongoLambdaQuery(Class<T> entityClass, MongoTemplate mongoTemplate) {
        super.setEntityClass(entityClass);
        super.initNeed();
        super.setMongoTemplate(mongoTemplate);
    }

    @Override
    protected String columnToString(SFunction<T, ?> column) {
        return LambdaUtils.getField(column);
    }
}
