package com.xww.hbase.spring.boot.starter.core;

import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
public interface HbaseOperations {

    /**
     * Executes the given action against the specified table handling resource management.
     * <p>
     * Application exceptions thrown by the action object get propagated to the caller (can only be unchecked).
     * Allows for returning a result object (typically a domain object or collection of domain objects).
     *
     * @param tableName the target table
     * @param <T>       action type
     * @return the result object of the callback action, or null
     */
    <T> T execute(String tableName, TableCallback<T> action);

    <T> List<T> find(Class<T> clazz);

    <T> List<T> find(String family, Class<T> clazz);

    <T> List<T> find(String family, String qualifier, Class<T> clazz);

    <T> List<T> find(Scan scan, Class<T> clazz);

    <T> List<T> find(List<Get> gets, Class<T> clazz);

    <T> T get(Get get, Class<T> clazz);

    <T> T get(String rowName, Class<T> clazz);

    <T> T get(String rowName, String familyName, Class<T> clazz);

    <T> T get(String rowName, String familyName, String qualifier, Class<T> clazz);

    /**
     * execute put update or delete
     *
     * @param tableName
     * @param action
     */
    void execute(String tableName, MutatorCallback action);

    <T> void saveOrUpdate(T table);

    <T> void saveOrUpdate(List<T> tableList);
}
