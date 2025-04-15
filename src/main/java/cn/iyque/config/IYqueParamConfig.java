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

    private String uploadDir;

    //文件预览访问前缀
    private String fileViewUrl;


    //是否预审所有数据,fasle默认预审当天0点到此刻的数据，true所有数据
    private Boolean inquiryAll=false;

    private String complaintUrl="https://iyque.cn";


    //向量数据库相关配置
    private VectorStoreParam vector;



    @Data
    public static class VectorStoreParam{


        private  String protocol;
        private  String host;
        private  String className;
    }
}
