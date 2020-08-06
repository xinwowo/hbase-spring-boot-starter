package com.xww.hbase.spring.boot.starter.annotation;

import java.lang.annotation.*;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String namespace();

    String name();

    String[] families();

    String value() default "";
}
