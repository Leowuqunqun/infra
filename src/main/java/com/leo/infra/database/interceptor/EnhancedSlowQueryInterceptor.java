package com.leo.infra.database.interceptor;

import com.leo.infra.database.LogLevel;
import com.leo.infra.database.enums.DataBaseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class EnhancedSlowQueryInterceptor implements Interceptor {

    // 全局配置
    public static class Config {
        public LogLevel logLevel = LogLevel.INFO;
        public int slowQueryThreshold = 100;  // 默认慢查询时间阈值为 100 毫秒
    }

    private final Config config;

    public EnhancedSlowQueryInterceptor(Config config) {
        this.config = config;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = invocation.proceed();
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        if (executeTime > config.slowQueryThreshold) {
            String method = invocation.getMethod().getName();
            String target = invocation.getTarget().getClass().getSimpleName();
            String sql = "";
            Object[] args = invocation.getArgs();
            if (args != null && args.length > 0) {
                sql = args[0].toString();
            }
            Connection connection = null;
            try {
                if (invocation.getTarget() instanceof StatementHandler) {
                    connection = ((Statement)result).getConnection();
                    DataBaseType dbType = getDatabaseType(connection);
                    if (dbType != null) {
                        String message = buildLogMessage(dbType, target, method, sql, executeTime);
                        handleLogLevel(config.logLevel, message);
                    }
                }
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }
        return result;
    }

    // 根据 connection 获取数据库类型
    private static DataBaseType getDatabaseType(Connection connection) throws SQLException {
        String productName = connection.getMetaData().getDatabaseProductName();
        if (productName != null && !productName.isEmpty()) {
            for (DataBaseType dbType : DataBaseType.values()) {
                if (dbType.getName().equalsIgnoreCase(productName)) {
                    return dbType;
                }
            }
        }
        return null;
    }

    // 构建日志信息字符串
    private static String buildLogMessage(DataBaseType dbType, String target, String method, String sql, long executeTime) {
        String message = "[%s] %s.%s() 执行 SQL 语句: %s，耗时: %d 毫秒";
        return String.format(message, dbType.getName(), target, method, sql, executeTime);
    }

    // 根据配置打印日志信息
    private static void handleLogLevel(LogLevel level, String message) {
        switch (level) {
            case TRACE:
                log.trace(message);
                break;
            case DEBUG:
                log.debug(message);
                break;
            case INFO:
                log.info(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case ERROR:
                log.error(message);
                break;
            default:
                break;
        }
    }
}