package cn.iyque.dao;

import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.IYqueUserCodeCountVo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IYQueCustomerInfoDao  extends JpaRepository<IYQueCustomerInfo,Integer> {
    IYQueCustomerInfo findByExternalUseridAndUserId(String externalUserid, String userId);


    List<IYQueCustomerInfo> findByExternalUserid(String externalUserid);
}
