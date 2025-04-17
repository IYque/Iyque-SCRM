package cn.iyque.dao;

import cn.iyque.entity.IYqueKfMsgSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueKfMsgSubDao extends JpaRepository<IYqueKfMsgSub,Long>, JpaSpecificationExecutor<IYqueKfMsgSub> {
}
