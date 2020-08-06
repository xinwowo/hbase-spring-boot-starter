package com.xww.hbase.spring.boot.starter.core;

import org.apache.hadoop.hbase.client.Result;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
public interface RowMapper<T> {

    T mapRow(Result result, int rowNum) throws Exception;
}
