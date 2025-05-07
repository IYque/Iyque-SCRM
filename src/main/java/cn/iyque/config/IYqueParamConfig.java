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



    //向量相关设置
    @Data
    public static class VectorStoreParam{

        //片段截取字符数,控制分片的长度，确保均匀分割
        private int chunkSize=1000;
        //重叠数	保留分片边界的上下文，避免语义断裂,通常设置为片段数的10%-20%之间
        private int chunkOverlap=200;

        //向量维度（需与实际数据匹配）
        private  Integer dimension=1536;

        //自定义集合名称
        private  String collectionName;

        //向量模型
        private String vectorModel;

        //余弦相似度
        private Float score=0.5f;

        //Milvus地址
        private String host;
        //Milvus端口
        private String port;
        //模型的apikey
        private String apiKey;
        //模型的请求地址
        private String apiUrl;

    }
}
