package com.xww.hbase.spring.boot.starter.core;

import org.apache.hadoop.hbase.client.Table;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
public interface TableCallback<T> {

    T doInTable(Table table) throws Throwable;
}
