package com.xww.hbase.spring.boot.starter;

import com.xww.hbase.spring.boot.starter.annotation.Row;
import com.xww.hbase.spring.boot.starter.annotation.RowKey;
import com.xww.hbase.spring.boot.starter.annotation.Table;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
@Data
@Builder
@Table(namespace = "test", name = "demo", families = {"info"})
public class DemoMapper {
    @RowKey
    @Row(family = "info")
    private String id;
    @Row(family = "info")
    private String name;
    @Row(family = "info")
    private Integer age;
    @Row(family = "info")
    private String desc;
    @Row(family = "info")
    private Date createdTime;
}
