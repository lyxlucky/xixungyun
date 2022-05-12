package com.lyx.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author liao
 * @date 2022/5/12 19:28
 */
@Data
@Component
@ConfigurationProperties(prefix = "monthsign")
@PropertySource(encoding = "GBK" ,value = "classpath:config/month.properties",ignoreResourceNotFound = true)
public class MonthSign {

    private String situation;

    private String grade;

    private String help;
}
