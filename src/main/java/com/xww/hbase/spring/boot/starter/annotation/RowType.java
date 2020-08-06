package com.xww.hbase.spring.boot.starter.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
@Getter
@AllArgsConstructor
public enum RowType {
    Short(Short.class.getTypeName()),
    Integer(Integer.class.getTypeName()),
    Long(Long.class.getTypeName()),
    Float(Float.class.getTypeName()),
    Double(Double.class.getTypeName()),
    Boolean(Boolean.class.getTypeName()),
    String(String.class.getTypeName()),
    BigDecimal(BigDecimal.class.getTypeName()),
    Date(Date.class.getTypeName());

    private String name;

    public static RowType get(String name) {
        if (StringUtils.isEmpty(name)) {
            return RowType.String;
        }
        for (RowType type : RowType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return RowType.String;
    }
}
