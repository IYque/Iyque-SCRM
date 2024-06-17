package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueDefaultMsgDao;
import cn.iyque.dao.IYqueUserCodeDao;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.domain.IYqueDefaultMsg;
import cn.iyque.domain.IYqueUserCode;
import cn.iyque.enums.RemarksType;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueDefaultMsgService;
import cn.iyque.strategy.callback.ActionContext;
import cn.iyque.strategy.callback.MakeTagCustomerStrategy;
import cn.iyque.strategy.callback.RemarkCustomerStrategy;
import cn.iyque.strategy.callback.SendWelcomeMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.bean.external.WxCpUpdateRemarkRequest;
import me.chanjar.weixin.cp.bean.external.WxCpWelcomeMsg;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import me.chanjar.weixin.cp.bean.external.msg.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class IYqueDefaultMsgImpl implements IYqueDefaultMsgService {

    @Autowired
    IYqueDefaultMsgDao iYqueDefaultMsgDao;

    @Autowired
    IYqueUserCodeDao iYqueUserCodeDao;


    @Autowired
    IYqueConfigService iYqueConfigService;



    @Override
    public IYqueDefaultMsg findDefaultMsg() {
        List<IYqueDefaultMsg> iYqueDefaultMsgs = iYqueDefaultMsgDao.findAll();
        if(CollectionUtil.isEmpty(iYqueDefaultMsgs)){
            return new IYqueDefaultMsg();
        }
        return iYqueDefaultMsgs.stream().findFirst().get();
    }

    @Override
    public void saveOrUpdate(IYqueDefaultMsg iYqueDefaultMsg) {
        iYqueDefaultMsgDao.saveAndFlush(iYqueDefaultMsg);
    }

    @Override
    public void callBackAction(IYqueCallBackBaseMsg callBackBaseMsg) {

        try {
            if(StrUtil.isNotEmpty(callBackBaseMsg.getState())){
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


                    }
                }else{
                    log.error("当前渠道活码不存在");
                }
            }

        }catch (Exception e){
            log.error("欢迎语动作执行异常:"+e.getMessage());

        }

    }



}
