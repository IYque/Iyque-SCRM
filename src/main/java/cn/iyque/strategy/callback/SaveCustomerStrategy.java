package cn.iyque.strategy.callback;

import cn.iyque.dao.IYQueCustomerInfoDao;
import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.domain.IYqueUserCode;
import cn.iyque.enums.CustomerStatusType;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 客户相关信息入库
 */
@Slf4j
public class SaveCustomerStrategy implements ActionStrategy{


    @Autowired
    private IYQueCustomerInfoDao iyQueCustomerInfoDao;

    @Override
    public void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYqueUserCode iYqueUserCode, WxCpExternalContactInfo contactDetail) {
        iyQueCustomerInfoDao.save(
                IYQueCustomerInfo.builder()
                        .state(callBackBaseMsg.getState())
                        .externalUserid(callBackBaseMsg.getExternalUserID())
                        .userId(callBackBaseMsg.getUserID())
                        .status(CustomerStatusType.CUSTOMER_STATUS_TYPE_COMMON.getCode())
                        .build()
        );
    }
}
