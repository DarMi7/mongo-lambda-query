package com.darmi.plugin.core;

import com.darmi.plugin.spring.SpringUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author darmi
 */
public abstract class MongoAbstractLambdaQuery<
        T, Children extends MongoAbstractLambdaQuery<T, Children>>
        extends AbstractQuery<T, SFunction<T, ?>, Children> {

    private MongoTemplate mongoTemplate;

    private List<Criteria> criterion;

    private List<Criteria> andCriterion;

    private Criteria andOrCriterion;

    private Query query;

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
            addCriteria();
            return new PageImpl(
                    mongoTemplate.find(query.with(pageable), entityClass),
                    pageable,
                    mongoTemplate.count(query, entityClass));
        }
        return new PageImpl(list());
    }

    @Override
    public T one() {
        return this.mongoTemplate.findOne(addCriteria(), entityClass);
    }

    @Override
    public List<T> list() {
        return this.mongoTemplate.find(addCriteria(), entityClass);
    }

    private Query addCriteria() {
        handleAndOrCriteria(criterion);
        criterion.stream().filter(Objects::nonNull).forEach(query::addCriteria);
        return query;
    }

    private void handleAndOrCriteria(List<Criteria> criterion) {
        if (andOrCriterion == null) return;
        if (!CollectionUtils.isEmpty(andCriterion)) {
            andOrCriterion.andOperator(andCriterion.toArray(new Criteria[0]));
        }
        criterion.add(andOrCriterion);
    }

    @Override
    public Children sort(SFunction<T, ?> column, Sort.Direction direction) {
        return typedThis;
    }

    @Override
    protected final void columnToSqlSegment(
            SFunction<T, ?> column, Object val, SqlKeyword keyWord, Object... key) {
        combineSqlSegment(val, keyWord, LambdaUtils.getField(column), key);
    }

    @Override
    protected void columnToSqlSegment(String column, Object val, SqlKeyword keyWord, Object... key) {
        combineSqlSegment(val, keyWord, column, key);
    }

    private void combineSqlSegment(Object val, SqlKeyword keyWord, String fieldName, Object[] key) {
        Criteria criteria = null;
        switch (keyWord) {
            case IS:
                criteria = Criteria.where(fieldName).is(val);
                break;
            case LIKE:
                Pattern pattern = Pattern.compile(val + ".*", Pattern.CASE_INSENSITIVE);
                criteria = Criteria.where(fieldName).regex(pattern);
                break;
            case IN:
                if (val instanceof List) {
                    criteria = Criteria.where(fieldName).in(((List<?>) val).toArray());
                    break;
                }
                criteria = Criteria.where(fieldName).in(val);
                break;
            case GT:
                criteria = Criteria.where(fieldName).gt(val);
                break;
            case GE:
                criteria = Criteria.where(fieldName).gte(val);
                break;
            case LT:
                criteria = Criteria.where(fieldName).lt(val);
                break;
            case LE:
                criteria = Criteria.where(fieldName).lte(val);
                break;
            case NE:
                criteria = Criteria.where(fieldName).ne(val);
                break;
            case ELEMMATCH:
                if (val instanceof ArrayList) {
                    criteria =
                            Criteria.where(fieldName)
                                    .elemMatch(Criteria.where(key[0].toString()).in(((ArrayList<?>) val)));
                }
                break;
            case REG:
                criteria = Criteria.where(fieldName).regex(val.toString(), "i");
                break;
            case OR_FRONT:
                orFrontCriteria();
                break;
            case AND:
                if (andOrCriterion == null) andOrCriterion = new Criteria();
                andCriterion = new ArrayList<>();
                break;
            default:
                throw new RuntimeException("#####columnToSqlSegment, not exist this function######");
        }
        if (andCriterion != null && criteria != null) {
            andCriterion.add(criteria);
            return;
        }
        if (criteria != null) criterion.add(criteria);
    }

    private void orFrontCriteria() {
        if (andOrCriterion == null) andOrCriterion = new Criteria();
        if (!CollectionUtils.isEmpty(criterion) && criterion.size() > 1) {
            andOrCriterion.orOperator(criterion.toArray(new Criteria[0]));
            criterion.clear();
        }
    }

    protected void initNeed() {
        mongoTemplate = SpringUtil.getBean(MongoTemplate.class);
        criterion = new ArrayList<>(8);
        query = new Query();
    }
}
