package com.darmi.plugin.core;

import static com.darmi.plugin.core.SortEnum.ASC;
import static com.darmi.plugin.core.SortEnum.DESC;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author darmi
 */
public class SqlSegment<T> {

  public static final long BATCH_SIZE = 10L;

  public static final long LIMIT = 10000L;

  public static final int INITIAL_CAPACITY = 8;

  private static final String ID_KEY = "_id";

  private final List<Criteria> criterion;

  private final Query query;

  private final Map<String, Object> convertToStringToRegexMatchMap;

  private List<Criteria> andCriterion;

  private Criteria orCriteria;


  public SqlSegment() {
    criterion = new ArrayList<>(INITIAL_CAPACITY);
    query = new Query();
    convertToStringToRegexMatchMap = new HashMap<>(INITIAL_CAPACITY);
  }

  public Query getQuery() {
    addCriteria();
    return query;
  }

  public Document getAggregateDocument(PaginationDTO pagination, Class<T> entityClass) {
    List<Document> pipeline = new ArrayList<>();
    addMatchDocument(pipeline);
    addFacetDocument(pagination, pipeline);
    return getAggregateDocument(pipeline, entityClass);
  }

  public void combine(Object val, SqlKeyword keyWord, String fieldName, Object[] key) {
    Criteria criteria = null;
    switch (keyWord) {
      case IS:
        if (fieldName.contains(ID_KEY)) {
          criteria = Criteria.where(fieldName).is(new ObjectId(val.toString()));
          break;
        }
        criteria = Criteria.where(fieldName).is(val);
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
        if (val instanceof Collection) {
          criteria =
              Criteria.where(fieldName)
                  .elemMatch(Criteria.where(key[0].toString()).in(((Collection<?>) val)));
        }
        break;
      case REG:
        if (!(val instanceof String)) {
          convertToStringToRegexMatchMap.put(fieldName, val);
        }
        criteria = Criteria.where(fieldName).regex(val.toString(), "i");
        break;
      case OR_FRONT:
        orFrontCriteria();
        break;
      case AND:
        andCriterion = new ArrayList<>();
        break;
      default:
        break;
    }
    if (andCriterion != null && criteria != null) {
      andCriterion.add(criteria);
      return;
    }
    if (criteria != null) {
      criterion.add(criteria);
    }
  }

  private void addCriteria() {
    handleOrCriteria();
    handleAndCriteria();
    criterion.stream().filter(Objects::nonNull).forEach(query::addCriteria);
  }

  private void handleOrCriteria() {
    if (orCriteria == null) {
      return;
    }
    criterion.add(orCriteria);
  }

  private void handleAndCriteria() {
    if (!CollectionUtils.isEmpty(andCriterion)) {
      Criteria andCriteria = new Criteria();
      andCriteria.andOperator(andCriterion.toArray(new Criteria[0]));
      criterion.add(andCriteria);
    }
  }

  private void addMatchDocument(List<Document> pipeline) {
    List<Document> match = getMatchCriterion();
    if (!CollectionUtils.isEmpty(match)) {
      pipeline.add(new Document("$match", new Document("$and", match)));
    }
  }

  private Document getAggregateDocument(List<Document> pipeline, Class<T> entityClass) {
    Document aggregate = new Document("aggregate", getCollectionName(entityClass));
    aggregate.append("pipeline", pipeline);
    aggregate.append("cursor", new Document("batchSize", BATCH_SIZE));
    return aggregate;
  }

  private void addFacetDocument(PaginationDTO pagination, List<Document> pipeline) {
    Document facet = new Document("metadata",
        Collections.singletonList(new Document("$count", "total")));
    if (pagination != null) {
      if (pagination.sortIsNotEmpty()) {
        pipeline.add(new Document("$sort", new Document(pagination.getSort().getSortBy(),
            DESC.getQueryCode().equals(pagination.getSort().getDirection()) ? DESC.getMongoCode()
                : ASC.getMongoCode())));
      }
      facet.append("data",
          Arrays.asList(new Document("$skip", pagination.getPage() * pagination.getPageSize()),
              new Document("$limit", pagination.getPageSize())));
    } else {
      facet.append("data", Collections.singletonList(new Document("$limit", LIMIT)));
    }
    pipeline.add(new Document("$facet", facet));
  }

  private List<Document> getMatchCriterion() {
    List<Document> match = new ArrayList<>();
    if (orCriteria != null) {
      addOrDocument(match);
    }
    handleAndCriteria();
    if (!CollectionUtils.isEmpty(criterion)) {
      addAndDocument(match, criterion);
    }
    return match;
  }

  private void addAndDocument(List<Document> match, List<Criteria> criterion) {
    List<Document> otherCriterion = criterion.stream()
        .map(e -> convertToString(e.getCriteriaObject()))
        .collect(Collectors.toList());
    match.addAll(otherCriterion);
  }

  private void addOrDocument(List<Document> match) {
    List<Document> orCriterion = orCriteria.getCriteriaObject().get("$or", List.class);
    List<Document> or = orCriterion.stream().map(this::convertToString)
        .collect(Collectors.toList());
    match.add(new Document("$or", or));
  }

  private Document convertToString(Document e) {
    for (String key : convertToStringToRegexMatchMap.keySet()) {
      if (e.get(key) != null) {
        return new Document("$expr", new Document("$regexMatch",
            new Document("input",
                new Document("$toString", String.format("$%s", key)))
                .append("regex", convertToStringToRegexMatchMap.get(key).toString())));
      }
    }
    return e;
  }

  private void orFrontCriteria() {
    if (!CollectionUtils.isEmpty(criterion) && criterion.size() > 1) {
      orCriteria = new Criteria();
      orCriteria.orOperator(criterion.toArray(new Criteria[0]));
      criterion.clear();
    }
  }

  private String getCollectionName(Class<T> entityClass) {
    org.springframework.data.mongodb.core.mapping.Document document = entityClass.getAnnotation(
        org.springframework.data.mongodb.core.mapping.Document.class);
    return StringUtils.hasText(document.value()) ? document.value() : document.collection();
  }

}
