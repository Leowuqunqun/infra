package com.leo.infra.database;

// 日志级别枚举
    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL;

        public boolean isEqualOrMoreSevereThan(LogLevel other) {
            return this.ordinal() >= other.ordinal();
        }
    }