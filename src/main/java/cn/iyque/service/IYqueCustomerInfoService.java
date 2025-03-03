package cn.iyque.service;

import cn.iyque.domain.*;

import java.util.List;

public interface IYqueCustomerInfoService {
    void addCustomerCallBackAction( IYqueCallBackBaseMsg callBackBaseMsg);
    void updateCustomerInfoStatus(String externalUserid,String userId,Integer status);

    IYqueUserCodeCountVo countTotalTab(IYQueCountQuery queCountQuery,boolean codeOrLink);

    IYQueTrendCount countTrend(IYQueCountQuery queCountQuery,boolean codeOrLink);

    List<IYQueCustomerInfo> saveCustomer(String externalUserid);


    IYQueCustomerInfo findCustomerInfoByExternalUserId(String externalUserid);

}
