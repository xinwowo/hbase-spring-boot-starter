package com.xww.hbase.spring.boot.starter;

import com.xww.hbase.spring.boot.starter.core.HbaseTemplate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(HbaseProperties.class)
@ConditionalOnBean(HbaseProperties.class)
public class HbaseConfiguration {

    private static final String HBASE_QUORUM = "hbase.zookeeper.quorum";
    private static final String HBASE_ROOTDIR = "hbase.rootdir";
    private static final String HBASE_ZNODE_PARENT = "zookeeper.znode.parent";

    @Autowired
    private HbaseProperties hbaseProperties;

    @Bean
    @ConditionalOnMissingBean(HbaseTemplate.class)
    public HbaseTemplate hbaseTemplate() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set(HBASE_QUORUM, this.hbaseProperties.getQuorum());
        Optional.ofNullable(hbaseProperties.getRootDir()).ifPresent(data -> configuration.set(HBASE_ROOTDIR, data));
        Optional.ofNullable(hbaseProperties.getNodeParent()).ifPresent(data -> configuration.set(HBASE_ZNODE_PARENT, data));
        return new HbaseTemplate(configuration);
    }
}
