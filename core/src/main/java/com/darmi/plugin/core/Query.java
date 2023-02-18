package com.darmi.plugin.core;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author darmi
 */
public interface Query<T> {
    T one();

    List<T> list();

    Page<T> page(PaginationDTO pagination);

}
