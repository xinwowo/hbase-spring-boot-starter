package com.xww.hbase.spring.boot.starter.mapping;

import com.xww.hbase.spring.boot.starter.annotation.Row;
import com.xww.hbase.spring.boot.starter.annotation.RowKey;
import com.xww.hbase.spring.boot.starter.annotation.Table;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
public class TableMappingContext {

    private final ConcurrentMap<Class<?>, TableMapping> tableMappingMap = new ConcurrentHashMap<>();

    public TableMapping getTableMapping(Class<?> clazz) {
        TableMapping entityMapping = tableMappingMap.get(clazz);
        if (entityMapping == null) {
            tableMappingMap.putIfAbsent(clazz, initTableMapping(clazz));
        }
        return entityMapping;
    }

    public TableMapping initTableMapping(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        TableMapping tableMapping = TableMapping.builder()
                .namespace(table.namespace())
                .tableName(table.name())
                .columnFamilies(table.families()).build();

        List<RowMapping> rowMappingList = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RowKey.class)) {
                RowKey rowKey = field.getAnnotation(RowKey.class);
                RowMapping rowMapping = RowMapping.builder()
                        .columnName(StringUtils.isEmpty(rowKey.name()) ? field.getName() : rowKey.name())
                        .javaColumnName(field.getName())
                        .columnType(StringUtils.isEmpty(rowKey.type()) ? field.getType().getName() : rowKey.type()).build();
                tableMapping.setRowKeyMapping(rowMapping);
            }
            if (field.isAnnotationPresent(Row.class)) {
                Row row = field.getAnnotation(Row.class);
                RowMapping rowMapping = RowMapping.builder()
                        .columnFamily(row.family())
                        .columnName(StringUtils.isEmpty(row.name()) ? field.getName() : row.name())
                        .javaColumnName(field.getName())
                        .columnType(StringUtils.isEmpty(row.type()) ? field.getType().getName() : row.type()).build();
                rowMappingList.add(rowMapping);
            }
        }
        tableMapping.setRowMappingList(rowMappingList);
        return tableMapping;
    }
}
