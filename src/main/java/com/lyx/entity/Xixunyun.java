package com.lyx.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liao
 * @date 2022/5/12 17:45
 */
@Component
@ConfigurationProperties(prefix = "xixunyun")
@Data
public class Xixunyun {
    private String username;
    private String password;
    private String school;
    private String familyName;
    private String familyPhone;

}
