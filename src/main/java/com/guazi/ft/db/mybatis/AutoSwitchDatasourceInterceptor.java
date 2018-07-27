package com.guazi.ft.db.mybatis;

import com.guazi.ft.common.JsonUtil;
import com.guazi.ft.db.annotation.DataSource;
import com.guazi.ft.db.aop.DataSourceHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * 用于实现主从分离
 *
 * @author shichunyang
 */
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        )
})
@Slf4j
public class AutoSwitchDatasourceInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        long start = System.currentTimeMillis();

        // 对主从的控制
        String dataSourceKey = DataSourceHolder.getDataSourceKey();
        if (dataSourceKey != null && dataSourceKey.equals(DataSource.master)) {
        } else {
            DataSourceHolder.setDataSourceKey(DataSource.slave);
        }

        Object objectValue = invocation.proceed();

        long end = System.currentTimeMillis();

        log.info("sql_id==>{}, sql==>{}, params==>{}, cost==>{}ms", sqlId, boundSql.getSql(), JsonUtil.object2Json(parameter), end - start);
        return objectValue;
    }

    @Override
    public Object plugin(Object o) {
        // 将目标对象封装成代理对象
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
