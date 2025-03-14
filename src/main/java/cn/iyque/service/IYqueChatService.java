package cn.iyque.service;

import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueChatCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueChatService {



    /**
     * 获取分页群数据
     * @param pageable
     * @return
     */
    Page<IYqueChat> findAll(String name, Pageable pageable);


    /**
     * 获取所有群数据
     * @return
     */
    List<IYqueChat> findAllIYqueChat();


    /**
     * 同步客群
     */
    void synchIyqueChat();
}
