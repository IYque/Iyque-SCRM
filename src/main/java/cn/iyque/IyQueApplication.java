package cn.iyque;


import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.sql.SQLException;

@SpringBootApplication
public class IyQueApplication {
    public static void main(String[] args) throws SQLException {
        Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
        new SpringApplicationBuilder(IyQueApplication.class)
                .build().run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ 源雀SCRM【开源版】启动成功。官方文档地址【https://iyque.cn】ლ(´ڡ`ლ)ﾞ");
    }
}
