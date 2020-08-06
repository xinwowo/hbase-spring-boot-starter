package com.xww.hbase.spring.boot.starter;

import com.xww.hbase.spring.boot.starter.core.HbaseTemplate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
public class TestCase {

    private static final HbaseTemplate hbaseTemplate;

    static {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "127.0.0.1:2181");
        hbaseTemplate = new HbaseTemplate(configuration);
    }

    @Test
    public void testInit() throws IOException {
        Admin admin = hbaseTemplate.getConnection().getAdmin();
        admin.deleteNamespace("test");
        admin.createNamespace(NamespaceDescriptor.create("test").build());
        admin.disableTable(TableName.valueOf("test:demo"));
        admin.deleteTable(TableName.valueOf("test:demo"));
        admin.createTable(new HTableDescriptor(TableName.valueOf("test:demo")).addFamily(new HColumnDescriptor("info")));
    }

    @Test
    public void testAdd() {
        hbaseTemplate.saveOrUpdate(Arrays.asList(DemoMapper.builder().id("1").name("jack1").age(1).createdTime(new Date()).build()
                , DemoMapper.builder().id("2").name("jack2").age(2).build()
                , DemoMapper.builder().id("3").name("jack3").age(3).build()
                , DemoMapper.builder().id("4").name("jack4").age(4).build()
                , DemoMapper.builder().id("5").name("jack5").age(5).build()
                , DemoMapper.builder().id("6").name("jack6").age(6).build()));
    }

    @Test
    public void find() {
        hbaseTemplate.find(new Scan().withStopRow(Bytes.toBytes("1"), true)
                .withStopRow(Bytes.toBytes("6"), true)
                .setFilter(new FilterList(FilterList.Operator.MUST_PASS_ONE, new RowFilter[]{
                        new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("2"))),
                        new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("3"))),
                        new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("4"))),
                        new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("5"))),
                        new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("6")))
                })), DemoMapper.class);
    }

    @Test
    public void get() {
        hbaseTemplate.get("1", DemoMapper.class);
    }
}
