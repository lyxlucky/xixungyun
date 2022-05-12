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
@ConfigurationProperties(prefix = "weeksign")
@PropertySource(encoding = "GBK" ,value = "classpath:config/week.properties",ignoreResourceNotFound = true)
public class WeekSign {

    private String situation;

    private String grade;

    private String help;

}
