package com.xww.hbase.spring.boot.starter;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
@Data
@Configuration
@ConditionalOnProperty(prefix = "spring.hbase", name = "enable", havingValue = "true", matchIfMissing = true)
@ConfigurationProperties(prefix = "spring.hbase")
public class HbaseProperties {

    private String quorum;

    private String rootDir;

    private String nodeParent;
}
