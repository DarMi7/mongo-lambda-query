package com.darmi.plugin.core;


public enum SortEnum {
  DESC("desc", -1),
  ASC("asc", 1);
  private String queryCode;
  private Integer mongoCode;

  public String getQueryCode() {
    return queryCode;
  }

  public Integer getMongoCode() {
    return mongoCode;
  }

  SortEnum(String queryCode, Integer mongoCode) {
    this.queryCode = queryCode;
    this.mongoCode = mongoCode;
  }
}
