package com.darmi.plugin.core;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;


public class SortDTO {
    private String direction;
    private String sortBy;

    public String getDirection() {
        return direction;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
