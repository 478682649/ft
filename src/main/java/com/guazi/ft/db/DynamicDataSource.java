package com.guazi.ft.db;

import com.guazi.ft.db.annotation.DataSource;
import com.guazi.ft.db.aop.DataSourceHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动态数据源
 *
 * @author shichunyang
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 从库key的集合
     */
    private List<String> slaveKeys = new ArrayList<>();

    /**
     * 程序基数器
     */
    private volatile AtomicInteger counter = new AtomicInteger(0);

    /**
     * 切换动态数据源
     *
     * @return 指定数据源的key
     */
    @Override
    public Object determineCurrentLookupKey() {

        String dataSourceKey = DataSourceHolder.getDataSourceKey();

        DataSourceHolder.clearDataSourceKey();

        if (dataSourceKey != null && dataSourceKey.equals(DataSource.master)) {
            return DataSource.master;
        } else if (dataSourceKey == null || dataSourceKey.equals(DataSource.slave)) {
            return this.getSlaveKey();
        } else {
            throw new RuntimeException("未知动态数据源==>" + dataSourceKey);
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
    }

    /**
     * 如果配置多个从库,则采取轮寻的方式获取
     *
     * @return 指定从库的key
     */
    public Object getSlaveKey() {

        if (counter.intValue() == Integer.MAX_VALUE) {

            synchronized (this) {
                if (counter.intValue() == Integer.MAX_VALUE) {
                    counter = new AtomicInteger(0);
                }
            }
        }

        int index = counter.getAndIncrement() % slaveKeys.size();

        return slaveKeys.get(index);
    }
}
