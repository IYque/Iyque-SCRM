package cn.iyque.dao;

import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.IYqueUserCodeCountVo;
import cn.iyque.entity.IYqueKfMsgSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IYQueCustomerInfoDao  extends JpaRepository<IYQueCustomerInfo,Long>, JpaSpecificationExecutor<IYQueCustomerInfo> {



    IYQueCustomerInfo findByExternalUseridAndUserId(String externalUserid, String userId);


    List<IYQueCustomerInfo> findByExternalUserid(String externalUserid);
}
