package com.darmi.plugin.core;


import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * @author darmi
 */
public interface Func<Children, R> extends Serializable {
  Children andBack();

  Children orFront();

  Children like(R column, Object val);

  Children is(String column, Object val);

  Children is(R column, Object val);

  Children lt(R column, Object val);

  Children le(R column, Object val);

  Children gt(R column, Object val);

  Children ne(R column, Object val);

  Children in(R column, List<?> values);

  Children in(String column, List<?> values);

  Children sort(R column, Sort.Direction direction);

  Children elemMatch(R column,Object val, Object... key) ;

  Children reg(R column, Object val);

  Children reg(String column, Object val);
}
