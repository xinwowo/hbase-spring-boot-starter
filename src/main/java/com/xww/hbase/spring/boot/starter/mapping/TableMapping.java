package com.xww.hbase.spring.boot.starter.mapping;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
@Data
@Builder
public class TableMapping {

    private String namespace;

    private String tableName;

    private String[] columnFamilies;

    private RowMapping rowKeyMapping;

    private List<RowMapping> rowMappingList;
}
