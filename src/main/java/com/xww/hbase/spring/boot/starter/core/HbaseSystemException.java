package com.xww.hbase.spring.boot.starter.core;

import org.springframework.core.NestedRuntimeException;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
public class HbaseSystemException extends NestedRuntimeException {

    public HbaseSystemException(Exception cause) {
        super(cause.getMessage(), cause);
    }

    public HbaseSystemException(Throwable throwable) {
        super(throwable.getMessage(), throwable);
    }

    public HbaseSystemException(String msg) {
        super(msg);
    }
}
