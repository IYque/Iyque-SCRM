package cn.iyque.dao;

import cn.iyque.entity.IYuqeOperateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYuqeOperateLogDao extends JpaRepository<IYuqeOperateLog,Long>, JpaSpecificationExecutor<IYuqeOperateLog> {
}
