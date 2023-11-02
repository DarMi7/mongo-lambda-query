package com.darmi.plugin.core;

import com.darmi.plugin.spring.SpringUtil;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

/**
 * @author darmi
 */
public abstract class MongoAbstractLambdaQuery<
        T, Children extends MongoAbstractLambdaQuery<T, Children>>
        extends AbstractQuery<T, SFunction<T, ?>, Children> {

    public static final int ZERO = 0;

    protected MongoTemplate mongoTemplate;

    private SqlSegment<T> sqlSegment;

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
            Query query = sqlSegment.getQuery();
            List<T> list = mongoTemplate.find(query.with(pageable), entityClass);
            rollBackPageConfig(query);
            long count = mongoTemplate.count(query, entityClass);
            return new PageImpl<>(list, pageable, count);
        }
        return new PageImpl<>(list());
    }

    @Override
    public Page<T> aggregate(PaginationDTO pagination) {
        Document aggregateDocument = sqlSegment.getAggregateDocument(pagination, entityClass);
        Document cursor = mongoTemplate.executeCommand(aggregateDocument).get("cursor", Document.class);
        return getResult(pagination, cursor);
    }

    @Override
    public T one() {
        return this.mongoTemplate.findOne(sqlSegment.getQuery(), entityClass);
    }

    @Override
    public List<T> list() {
        return this.mongoTemplate.find(sqlSegment.getQuery(), entityClass);
    }


    @Override
    public Children sort(SFunction<T, ?> column, Sort.Direction direction) {
        return typedThis;
    }

    @Override
    protected final void columnToSqlSegment(
        SFunction<T, ?> column, Object val, SqlKeyword keyWord, Object... key) {
        sqlSegment.combine(val, keyWord, columnToString(column), key);
    }

    @Override
    protected void columnToSqlSegment(String column, Object val, SqlKeyword keyWord, Object... key) {
        sqlSegment.combine(val, keyWord, column, key);
    }

    @Override
    protected String columnToString(SFunction<T, ?> column) {
        return LambdaUtils.getField(column);
    }

    protected void initNeed() {
        mongoTemplate = SpringUtil.getBean(MongoTemplate.class);
        sqlSegment = new SqlSegment<>();
    }

    private void rollBackPageConfig(Query query) {
        query.skip(ZERO);
        query.limit(Integer.MAX_VALUE);
    }

    private PageImpl<T> getResult(PaginationDTO pagination, Document cursor) {
        List<Document> firstBatch = cursor.get("firstBatch", List.class);
        if (CollectionUtils.isEmpty(firstBatch)) {
            return new PageImpl<>(Collections.emptyList());
        }
        List<Document> metadata = firstBatch.get(ZERO).get("metadata", List.class);
        List<T> list = getContent(firstBatch);
        cursor.clear();
        int total = CollectionUtils.isEmpty(metadata) ? ZERO : metadata.get(ZERO).getInteger("total");
        return pagination == null ? new PageImpl<>(list)
            : new PageImpl<>(list, PageRequest.of(pagination.getPage(), pagination.getPageSize()),
                total);
    }

    private List<T> getContent(List<Document> firstBatch) {
        List<Document> data = firstBatch.get(ZERO).get("data", List.class);
        return CollectionUtils.isEmpty(data) ? Collections.emptyList()
            : data.stream().map(e -> mongoTemplate.getConverter().read(entityClass, e))
                .collect(Collectors.toList());
    }

}
