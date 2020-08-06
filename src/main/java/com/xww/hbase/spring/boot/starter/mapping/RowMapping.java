package com.xww.hbase.spring.boot.starter.mapping;

import lombok.Builder;
import lombok.Data;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
@Data
@Builder
public class RowMapping {

    private String columnFamily;

    private String columnName;

    private String javaColumnName;

    private String columnType;

    private String columnValue;
}
