package cn.iyque;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.sql.SQLException;

@SpringBootApplication
@MapperScan("cn.iyque.mapper")
public class IyQueApplication {
    public static void main(String[] args) throws SQLException {
        new SpringApplicationBuilder(IyQueApplication.class)
                .build().run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ 源雀SCRM【开源版】启动成功。官方文档地址【https://iyque.cn】ლ(´ڡ`ლ)ﾞ");
    }
}
