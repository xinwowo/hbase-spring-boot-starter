package com.xww.hbase.spring.boot.starter.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.xww.hbase.spring.boot.starter.annotation.RowType;
import com.xww.hbase.spring.boot.starter.mapping.RowMapping;
import com.xww.hbase.spring.boot.starter.mapping.TableMapping;
import com.xww.hbase.spring.boot.starter.mapping.TableMappingContext;
import com.xww.hbase.spring.boot.starter.util.DateUtils;
import com.xww.hbase.spring.boot.starter.util.JsonUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
@Slf4j
public class HbaseTemplate implements HbaseOperations {

    private Configuration configuration;

    private volatile Connection connection;

    private TableMappingContext tableMappingContext;

    public HbaseTemplate(Configuration configuration) {
        this.setConfiguration(configuration);
        this.tableMappingContext = new TableMappingContext();
        Assert.notNull(configuration, "Valid configuration is required");
    }

    @Override
    public <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        StopWatch sw = new StopWatch();
        sw.start();
        Table table = null;
        try {
            table = this.getConnection().getTable(TableName.valueOf(tableName));
            return action.doInTable(table);
        } catch (Throwable throwable) {
            throw new HbaseSystemException(throwable);
        } finally {
            if (null != table) {
                try {
                    table.close();
                    sw.stop();
                } catch (IOException e) {
                    log.error("Hbase resources release fail");
                }
            }
        }
    }

    @Override
    public <T> List<T> find(Class<T> clazz) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        return this.find(scan, clazz);
    }

    @Override
    public <T> List<T> find(String family, Class<T> clazz) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addFamily(Bytes.toBytes(family));
        return this.find(scan, clazz);
    }

    @Override
    public <T> List<T> find(String family, String qualifier, Class<T> clazz) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return this.find(scan, clazz);
    }

    @Override
    public <T> List<T> find(Scan scan, Class<T> clazz) {
        TableMapping tableMapping = tableMappingContext.getTableMapping(clazz);
        return this.execute(tableMapping.getNamespace() + ":" + tableMapping.getTableName(), table -> {
            int caching = scan.getCaching();
            if (caching == 1) {
                scan.setCaching(5000);
            }
            ResultScanner scanner = table.getScanner(scan);
            try {
                List<T> rs = new ArrayList<>();
                for (Result result : scanner) {
                    rs.add(getRow(result, tableMapping.getRowMappingList(), clazz));
                }
                return rs;
            } finally {
                scanner.close();
            }
        });
    }

    @Override
    public <T> List<T> find(List<Get> gets, Class<T> clazz) {
        TableMapping tableMapping = tableMappingContext.getTableMapping(clazz);
        return this.execute(tableMapping.getNamespace() + ":" + tableMapping.getTableName(), table -> {
            Result[] scanner = table.get(gets);
            List<T> rs = new ArrayList<>();
            for (Result result : scanner) {
                rs.add(getRow(result, tableMapping.getRowMappingList(), clazz));
            }
            return rs;
        });
    }

    @Override
    public <T> T get(Get get, Class<T> clazz) {
        TableMapping tableMapping = tableMappingContext.getTableMapping(clazz);
        return this.execute(tableMapping.getNamespace() + ":" + tableMapping.getTableName(), table -> {
            return getRow(table.get(get), tableMapping.getRowMappingList(), clazz);
        });
    }

    @Override
    public <T> T get(String rowName, Class<T> clazz) {
        return this.get(rowName, null, null, clazz);
    }

    @Override
    public <T> T get(String rowName, String familyName, Class<T> clazz) {
        return this.get(rowName, familyName, null, clazz);
    }

    @Override
    public <T> T get(String rowName, String familyName, String qualifier, Class<T> clazz) {
        TableMapping tableMapping = tableMappingContext.getTableMapping(clazz);
        return this.execute(tableMapping.getNamespace() + ":" + tableMapping.getTableName(), table -> {
            Get get = new Get(Bytes.toBytes(rowName));
            if (StringUtils.isNotBlank(familyName)) {
                byte[] family = Bytes.toBytes(familyName);
                if (StringUtils.isNotBlank(qualifier)) {
                    get.addColumn(family, Bytes.toBytes(qualifier));
                } else {
                    get.addFamily(family);
                }
            }
            return getRow(table.get(get), tableMapping.getRowMappingList(), clazz);
        });
    }

    private <T> T getRow(Result result, List<RowMapping> rowMappingList, Class<T> clazz) {
        JSONObject jsonObject = new JSONObject();
        for (RowMapping rowMapping : rowMappingList) {
            byte[] columnValueBytes = result.getValue(Bytes.toBytes(rowMapping.getColumnFamily()),
                    Bytes.toBytes(rowMapping.getColumnName()));
            if (columnValueBytes == null) {
                continue;
            }
            String columnName = rowMapping.getJavaColumnName();
            String columnType = rowMapping.getColumnType();
            switch (RowType.get(columnType)) {
                case Short:
                    jsonObject.put(columnName, Bytes.toShort(columnValueBytes));
                    break;
                case Integer:
                    jsonObject.put(columnName, Bytes.toInt(columnValueBytes));
                    break;
                case Long:
                    jsonObject.put(columnName, Bytes.toLong(columnValueBytes));
                    break;
                case Float:
                    jsonObject.put(columnName, Bytes.toFloat(columnValueBytes));
                    break;
                case Double:
                    jsonObject.put(columnName, Bytes.toDouble(columnValueBytes));
                    break;
                case Boolean:
                    jsonObject.put(columnName, Bytes.toBoolean(columnValueBytes));
                    break;
                case String:
                    jsonObject.put(columnName, Bytes.toString(columnValueBytes));
                    break;
                case BigDecimal:
                    jsonObject.put(columnName, Bytes.toBigDecimal(columnValueBytes));
                    break;
                case Date:
                    jsonObject.put(columnName,
                            DateUtils.stringToDate(Bytes.toString(columnValueBytes), DateUtils.Format_1));
                    break;
                default:
                    log.error("Unsupported data types:" + columnType);
                    throw new HbaseSystemException("Unsupported data types: " + columnType);
            }
        }
        return JsonUtils.toObject(JsonUtils.toJson(jsonObject), clazz);
    }

    @Override
    public void execute(String tableName, MutatorCallback action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        StopWatch sw = new StopWatch();
        sw.start();
        BufferedMutator mutator = null;
        try {
            BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));
            mutator = this.getConnection().getBufferedMutator(mutatorParams.writeBufferSize(3 * 1024 * 1024));
            action.doInMutator(mutator);
        } catch (Throwable throwable) {
            sw.stop();
            throw new HbaseSystemException(throwable);
        } finally {
            if (null != mutator) {
                try {
                    mutator.flush();
                    mutator.close();
                    sw.stop();
                } catch (IOException e) {
                    log.error("Hbase mutator resources release fail");
                }
            }
        }
    }

    @Override
    public <T> void saveOrUpdate(T table) {
        TableMapping tableMapping = tableMappingContext.getTableMapping(table.getClass());
        this.execute(tableMapping.getNamespace() + ":" + tableMapping.getTableName(), mutator -> {
            mutator.mutate(put(table));
        });
    }

    @Override
    public <T> void saveOrUpdate(List<T> tableList) {
        List<Mutation> mutations = new ArrayList<>();
        for (T t : tableList) {
            mutations.add(put(t));
        }
        TableMapping tableMapping = tableMappingContext.getTableMapping(tableList.get(0).getClass());
        this.execute(tableMapping.getNamespace() + ":" + tableMapping.getTableName(), mutator -> {
            mutator.mutate(mutations);
        });
    }

    private <T> Put put(T t) {
        TableMapping tableMapping = tableMappingContext.getTableMapping(t.getClass());
        RowMapping rowKeyMapping = tableMapping.getRowKeyMapping();
        List<RowMapping> rowMappingList = tableMapping.getRowMappingList();
        Put put = null;
        try {
            Field field = t.getClass().getDeclaredField(rowKeyMapping.getJavaColumnName());
            field.setAccessible(true);
            if (field.get(t) == null) {
                throw new HbaseSystemException("RowKey cannot be null");
            }
            String columnType = field.getType().getTypeName();
            switch (RowType.get(columnType)) {
                case Short:
                    put = new Put(Bytes.toBytes((Short) field.get(t)));
                    break;
                case Integer:
                    put = new Put(Bytes.toBytes((Integer) field.get(t)));
                    break;
                case Long:
                    put = new Put(Bytes.toBytes((Long) field.get(t)));
                    break;
                case Float:
                    put = new Put(Bytes.toBytes((Float) field.get(t)));
                    break;
                case Double:
                    put = new Put(Bytes.toBytes((Double) field.get(t)));
                    break;
                case Boolean:
                    put = new Put(Bytes.toBytes((Boolean) field.get(t)));
                    break;
                case String:
                    put = new Put(Bytes.toBytes((String) field.get(t)));
                    break;
                case BigDecimal:
                    put = new Put(Bytes.toBytes((BigDecimal) field.get(t)));
                    break;
                case Date:
                    put = new Put(Bytes.toBytes(DateUtils.dateToString((Date) field.get(t), DateUtils.Format_1)));
                    break;
                default:
                    log.error("Unsupported data types:" + columnType);
                    throw new HbaseSystemException("Unsupported data types: " + columnType);
            }
            for (RowMapping rowMapping : rowMappingList) {
                field = t.getClass().getDeclaredField(rowMapping.getJavaColumnName());
                field.setAccessible(true);
                if (field.get(t) == null) {
                    continue;
                }
                byte[] columnFamily = Bytes.toBytes(rowMapping.getColumnFamily());
                byte[] columnName = Bytes.toBytes(rowMapping.getColumnName());
                columnType = field.getType().getTypeName();
                switch (RowType.get(columnType)) {
                    case Short:
                        put.addColumn(columnFamily, columnName, Bytes.toBytes((Short) field.get(t)));
                        break;
                    case Integer:
                        put.addColumn(columnFamily, columnName, Bytes.toBytes((Integer) field.get(t)));
                        break;
                    case Long:
                        put.addColumn(columnFamily, columnName, Bytes.toBytes((Long) field.get(t)));
                        break;
                    case Float:
                        put.addColumn(columnFamily, columnName, Bytes.toBytes((Float) field.get(t)));
                        break;
                    case Double:
                        put.addColumn(columnFamily, columnName, Bytes.toBytes((Double) field.get(t)));
                        break;
                    case Boolean:
                        put.addColumn(columnFamily, columnName, Bytes.toBytes((Boolean) field.get(t)));
                        break;
                    case String:
                        put.addColumn(columnFamily, columnName, Bytes.toBytes((String) field.get(t)));
                        break;
                    case BigDecimal:
                        put.addColumn(columnFamily, columnName, Bytes.toBytes((BigDecimal) field.get(t)));
                        break;
                    case Date:
                        put.addColumn(columnFamily, columnName,
                                Bytes.toBytes(DateUtils.dateToString((Date) field.get(t), DateUtils.Format_1)));
                        break;
                    default:
                        log.error("Unsupported data types:" + columnType);
                        throw new HbaseSystemException("Unsupported data types: " + columnType);
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return put;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Connection getConnection() {
        if (null == this.connection) {
            synchronized (this) {
                if (null == this.connection) {
                    try {
                        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(200, Integer.MAX_VALUE, 60L,
                                TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
                        // init pool
                        poolExecutor.prestartCoreThread();
                        this.connection = ConnectionFactory.createConnection(configuration, poolExecutor);
                    } catch (IOException e) {
                        log.error("Hbase connection release fail");
                    }
                }
            }
        }
        return this.connection;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

}
