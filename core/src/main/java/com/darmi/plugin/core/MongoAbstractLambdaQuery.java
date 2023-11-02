package com.darmi.plugin.core;

import com.darmi.plugin.spring.SpringUtil;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author darmi
 */
public abstract class MongoAbstractLambdaQuery<
        T, Children extends MongoAbstractLambdaQuery<T, Children>>
        extends AbstractQuery<T, SFunction<T, ?>, Children> {

    private MongoTemplate mongoTemplate;

    private CombineSqlSegment combineSqlSegment;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<T> page(PaginationDTO pagination) {
        if (pagination != null) {
            Pageable pageable;
            if (pagination.sortIsNotEmpty()) {
                pageable =
                        PageRequest.of(
                                pagination.getPage(),
                                pagination.getPageSize(),
                                Sort.by(
                                        Sort.Direction.fromString(pagination.getSort().getDirection()),
                                        pagination.getSort().getSortBy()));
            } else {
                pageable = PageRequest.of(pagination.getPage(), pagination.getPageSize());
            }
            Query query = combineSqlSegment.getQuery();
            return new PageImpl<>(
                mongoTemplate.find(query.with(pageable), entityClass),
                pageable,
                mongoTemplate.count(query, entityClass));
        }
        return new PageImpl<>(list());
    }

    @Override
    public T one() {
        Query query = combineSqlSegment.getQuery();
        return this.mongoTemplate.findOne(query, entityClass);
    }

    @Override
    public List<T> list() {
        Query query = combineSqlSegment.getQuery();
        return this.mongoTemplate.find(query, entityClass);
    }


    @Override
    public Children sort(SFunction<T, ?> column, Sort.Direction direction) {
        return typedThis;
    }

    @Override
    protected final void columnToSqlSegment(
            SFunction<T, ?> column, Object val, SqlKeyword keyWord, Object... key) {
        combineSqlSegment.combineSqlSegment(val, keyWord, LambdaUtils.getField(column), key);
    }

    @Override
    protected void columnToSqlSegment(String column, Object val, SqlKeyword keyWord, Object... key) {
        combineSqlSegment.combineSqlSegment(val, keyWord, column, key);
    }

    protected void initNeed(){
        mongoTemplate = SpringUtil.getBean(MongoTemplate.class);
        combineSqlSegment = new CombineSqlSegment();
    }
}
