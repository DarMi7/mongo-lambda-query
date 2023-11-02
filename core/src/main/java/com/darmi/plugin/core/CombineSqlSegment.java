package com.darmi.plugin.core;

import com.darmi.plugin.spring.SpringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

/**
 * @author darmi
 */
public class CombineSqlSegment {

  private final List<Criteria> criterion;

  private final Query query;

  private List<Criteria> andCriterion;

  private Criteria orCriteria;

  public CombineSqlSegment() {
    criterion = new ArrayList<>(8);
    query = new Query();
  }

  public Query getQuery() {
    return addCriteria();
  }

  public void combineSqlSegment(Object val, SqlKeyword keyWord, String fieldName, Object[] key) {
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
        andCriterion = new ArrayList<>();
        break;
      default:
        throw new RuntimeException("#####columnToSqlSegment, not exist this function######");
    }
    if (andCriterion != null && criteria != null) {
      andCriterion.add(criteria);
      return;
    }
    if (criteria != null) {
      criterion.add(criteria);
    }
  }


  private Query addCriteria() {
    handleOrCriteria();
    handleAndCriteria();
    criterion.stream().filter(Objects::nonNull).forEach(query::addCriteria);
    return query;
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

  private void orFrontCriteria() {
    if (!CollectionUtils.isEmpty(criterion) && criterion.size() > 1) {
      orCriteria = new Criteria();
      orCriteria.orOperator(criterion.toArray(new Criteria[0]));
      criterion.clear();
    }
  }
}
