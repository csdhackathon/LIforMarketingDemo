package com.ars.orm.types;

public enum ColumnType {
    VARCHAR("VARCHAR"),
    INTEGER("INTEGER"),
    BLOB("BLOB"),
    MANY2ONE("INTEGER"),
    DATETIME("varchar"),
    BOOLEAN("boolean");


    String type;

    ColumnType(String type) {
        this.type = type;
    }
}
