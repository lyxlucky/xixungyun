package com.lyx.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author liao
 * @date 2022/5/12 18:29
 */
@Data
@Component
@ConfigurationProperties(prefix = "daysign")
@PropertySource(encoding = "GBK" ,value = "classpath:config/day.properties",ignoreResourceNotFound = true)
public class DaySign {

    private String situation;

    private String grade;

    private String help;

}
