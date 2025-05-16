package cn.iyque.mass;

import cn.iyque.mass.sender.AbstractMassSender;
import cn.iyque.mass.sender.GroupMassSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MassSenderFactoryService {
    public  AbstractMassSender createSender(String chatType) {
        switch (chatType) {
            case "group"://客群群发
                return new GroupMassSender();
            default:
                log.error("不支持的群发类型");
                throw new IllegalArgumentException("不支持的群发类型");
        }

    }

    }
