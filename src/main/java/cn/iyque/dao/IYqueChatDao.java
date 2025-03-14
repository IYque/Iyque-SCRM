package cn.iyque.dao;

import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueChatDao extends JpaRepository<IYqueChat,Long> , JpaSpecificationExecutor<IYqueChat> {
}
