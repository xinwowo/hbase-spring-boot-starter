package com.xww.hbase.spring.boot.starter.core;

import org.apache.hadoop.hbase.client.BufferedMutator;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
public interface MutatorCallback {

    void doInMutator(BufferedMutator mutator) throws Throwable;
}
