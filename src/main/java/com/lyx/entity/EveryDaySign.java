package com.lyx.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author liao
 * @date 2022/5/14 10:13
 */
@Data
@Component
@ConfigurationProperties(prefix = "everydaysign")
@PropertySource(encoding = "GBK" ,value = "classpath:config/everydaysign.properties",ignoreResourceNotFound = true)
public class EveryDaySign {

    private String longitude;
    private String latitude;
    private String address;
    private String addressName;

}
