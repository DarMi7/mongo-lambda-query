package com.darmi.plugin.core;

import org.springframework.util.ObjectUtils;


public class PaginationDTO {
    private Integer page;
    private Integer pageSize;
    private SortDTO sort;

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setSort(SortDTO sort) {
        this.sort = sort;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public SortDTO getSort() {
        return sort;
    }

    public boolean sortIsNotEmpty() {
        return !ObjectUtils.isEmpty(this.sort);
    }

}
