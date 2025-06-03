package cn.iyque.service;

import cn.iyque.domain.*;
import cn.iyque.entity.IYqueKfMsgSub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueCustomerInfoService {
    void addCustomerCallBackAction( IYqueCallBackBaseMsg callBackBaseMsg);
    void updateCustomerInfoStatus(String externalUserid,String userId,Integer status);

    IYqueUserCodeCountVo countTotalTab(IYQueCountQuery queCountQuery,boolean codeOrLink);

    IYQueTrendCount countTrend(IYQueCountQuery queCountQuery,boolean codeOrLink);

    List<IYQueCustomerInfo> saveCustomer(String externalUserid);


    IYQueCustomerInfo findCustomerInfoByExternalUserId(String externalUserid);


    /**
     * 客户列表
     * @param iyQueCustomerInfo
     * @param pageable
     * @return
     */
    Page<IYQueCustomerInfo> findAll(IYQueCustomerInfo iyQueCustomerInfo, Pageable pageable);

    /**
     * 同步客户
     */
    void synchCustomer();

}
