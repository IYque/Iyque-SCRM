package cn.iyque.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.dao.IYQueCustomerInfoDao;
import cn.iyque.dao.IYqueUserCodeDao;
import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.domain.IYqueUserCode;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueCustomerInfoService;
import cn.iyque.strategy.callback.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class IYqueCustomerInfoServiceImpl implements IYqueCustomerInfoService {

    @Autowired
    IYqueUserCodeDao iYqueUserCodeDao;

    @Autowired
    IYqueConfigService iYqueConfigService;

    @Autowired
    IYQueCustomerInfoDao iyQueCustomerInfoDao;


    @Override
    public void addCustomerCallBackAction(IYqueCallBackBaseMsg callBackBaseMsg) {

        try {
            if(StrUtil.isNotEmpty(callBackBaseMsg.getState())&&callBackBaseMsg.getState().startsWith(CodeStateConstant.USER_CODE_STATE)){
                IYqueUserCode iYqueUserCode = iYqueUserCodeDao.findByCodeState(callBackBaseMsg.getState());
                if(null != iYqueUserCode){
                    WxCpExternalContactInfo contactDetail = iYqueConfigService.findWxcpservice().getExternalContactService()
                            .getContactDetail(callBackBaseMsg.getExternalUserID(), null);
                    if(null != contactDetail){
                        //发送欢迎语
                        ActionContext actionContext = new ActionContext(new SendWelcomeMsgStrategy());
                        actionContext.executeStrategy(callBackBaseMsg,iYqueUserCode,contactDetail);


                        //自动打标签
                        if (StrUtil.isNotEmpty(iYqueUserCode.getTagId())) {
                            actionContext.setActionStrategy(new MakeTagCustomerStrategy());
                            actionContext.executeStrategy(callBackBaseMsg,iYqueUserCode,contactDetail);
                        }

                        //自动备注
                        if (null != iYqueUserCode.getRemarkType()) {
                            actionContext.setActionStrategy(new RemarkCustomerStrategy());
                            actionContext.executeStrategy(callBackBaseMsg,iYqueUserCode,contactDetail);
                        }

                        //客户相关信息入库
                        actionContext.setActionStrategy(new SaveCustomerStrategy());
                        actionContext.executeStrategy(callBackBaseMsg,iYqueUserCode,contactDetail);

                    }
                }else{
                    log.error("当前渠道活码不存在");
                }
            }

        }catch (Exception e){
            log.error("欢迎语动作执行异常:"+e.getMessage());

        }

    }

    @Override
    public void updateCustomerInfoStatus(String externalUserid, String userId, Integer status) {
        IYQueCustomerInfo iyQueCustomerInfo
                = iyQueCustomerInfoDao.findByExternalUseridAndUserId(externalUserid, userId);

        if(null != iyQueCustomerInfo){
            iyQueCustomerInfo.setStatus(status);
            iyQueCustomerInfoDao.saveAndFlush(
                    iyQueCustomerInfo
            );

        }
    }


}
