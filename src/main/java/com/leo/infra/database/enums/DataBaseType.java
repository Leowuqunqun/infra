package com.leo.infra.database.enums;

// 数据库类型枚举
public enum DataBaseType {
    MYSQL("MySQL"),
    POSTGRESQL("PostgreSQL"),
    MONGODB("MongoDB");
    
    String name;
    
    DataBaseType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}