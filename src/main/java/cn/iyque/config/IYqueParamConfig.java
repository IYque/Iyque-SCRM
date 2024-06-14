package cn.iyque.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "iyque")
@Data
public class IYqueParamConfig {

    private String userName;
    private String pwd;

    private Boolean demo=false;
}
