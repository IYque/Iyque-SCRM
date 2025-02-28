package cn.iyque.dao;

import cn.iyque.entity.IYqueMsgAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IYqueMsgAuditDao extends JpaRepository<IYqueMsgAudit,String> {

    IYqueMsgAudit findTopByOrderByDataSeqDesc();

}
