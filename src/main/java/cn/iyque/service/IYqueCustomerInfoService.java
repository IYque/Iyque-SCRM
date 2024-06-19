package cn.iyque.service;

import cn.iyque.domain.IYqueCallBackBaseMsg;

public interface IYqueCustomerInfoService {
    void addCustomerCallBackAction( IYqueCallBackBaseMsg callBackBaseMsg);
    void updateCustomerInfoStatus(String externalUserid,String userId,Integer status);
}
