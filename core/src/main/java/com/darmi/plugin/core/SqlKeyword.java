package com.darmi.plugin.core;


/**
 * @author darmi
 */

public enum SqlKeyword{
    IS("IS"),
    OR_FRONT("OR"),
    AND("AND"),
    IN("IN"),
    NE("NE"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    ELEMMATCH("elemMatch"),
    REG("reg");

    private final String keyword;

    SqlKeyword(String keyword) {
        this.keyword = keyword;
    }
}
