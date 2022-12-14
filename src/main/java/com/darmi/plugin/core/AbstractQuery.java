package com.darmi.plugin.core;


import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.darmi.plugin.core.SqlKeyword.*;


/**
 * @author darmi
 */
public abstract class AbstractQuery<T, R, Children extends AbstractQuery<T, R, Children>>
    implements Func<Children, R>, Query<T> {

  protected final Children typedThis = (Children) this;

  protected Class<T> entityClass;

  public void setEntityClass(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  @Override
  public Children is(R column, Object val) {
    return addCondition(!ObjectUtils.isEmpty(val), column, IS, val);
  }

  @Override
  public Children like(R column, Object val) {
    return addCondition(!ObjectUtils.isEmpty(val), column, LIKE, val);
  }

  @Override
  public Children ne(R column, Object val) {
    return addCondition(!ObjectUtils.isEmpty(val), column, NE, val);
  }

  @Override
  public Children orFront() {
    return addCondition(true, null, OR_FRONT, null);
  }


  @Override
  public Children andBack() {
    return addCondition(true, null, AND_BACK, null);
  }

  @Override
  public Children in(String column, List<?> values) {
    if (ObjectUtils.isEmpty(values)) return typedThis;
    columnToSqlSegment(column, values, IN);
    return typedThis;
  }

  @Override
  public Children lt(R column, Object val) {
    return addCondition(!ObjectUtils.isEmpty(val), column, LT, val);
  }

  @Override
  public Children le(R column, Object val) {
    return addCondition(!ObjectUtils.isEmpty(val), column, LE, val);
  }

  @Override
  public Children gt(R column, Object val) {
    return addCondition(!ObjectUtils.isEmpty(val), column, GT, val);
  }

  @Override
  public Children in(R column, List<?> values) {
    return addCondition(!ObjectUtils.isEmpty(values), column, IN, values);
  }

  @Override
  public Children reg(R column, Object val) {
    return addCondition(!ObjectUtils.isEmpty(val), column, REG, val);
  }

  @Override
  public Children reg(String column, Object val) {
    if (ObjectUtils.isEmpty(val)) return typedThis;
    columnToSqlSegment(column, val, REG);
    return typedThis;
  }

  @Override
  public Children is(String column, Object val) {
    if (ObjectUtils.isEmpty(val)) return typedThis;
    columnToSqlSegment(column, val, IS);
    return typedThis;
  }

  @Override
  public Children elemMatch(R column, Object val, Object... key) {
    return addCondition(!ObjectUtils.isEmpty(val), column, ELEMMATCH, val, key);
  }

  protected Children addCondition(
      boolean condition, R column, SqlKeyword keyWord, Object val, Object... key) {
    return maybeDo(condition, () -> columnToSqlSegment(column, val, keyWord, key));
  }

  protected abstract void columnToSqlSegment(
      R column, Object val, SqlKeyword keyWord, Object... key);

  protected abstract void columnToSqlSegment(
      String column, Object val, SqlKeyword keyWord, Object... key);

  protected final Children maybeDo(boolean condition, DoSomething something) {
    if (condition) {
      something.doIt();
    }
    return typedThis;
  }

  protected abstract String columnToString(SFunction<T, ?> column) throws Exception;

  @FunctionalInterface
  public interface DoSomething {
    void doIt();
  }
}
