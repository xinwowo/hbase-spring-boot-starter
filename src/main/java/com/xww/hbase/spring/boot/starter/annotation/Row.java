package com.xww.hbase.spring.boot.starter.annotation;

import java.lang.annotation.*;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Row {
    String family();

    String name() default "";

    String type() default "";

    String value() default "";
}
