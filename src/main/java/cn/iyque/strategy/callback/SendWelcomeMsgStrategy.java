package cn.iyque.strategy.callback;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueDefaultMsg;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueDefaultMsgService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.bean.external.WxCpWelcomeMsg;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import me.chanjar.weixin.cp.bean.external.msg.Text;
import cn.iyque.constant.IYqueContant;
import cn.iyque.entity.IYqueUserCode;
/**
 * 发送欢迎语
 */
@Slf4j
public class SendWelcomeMsgStrategy implements ActionStrategy {

    @Override
    public void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYqueUserCode iYqueUserCode, WxCpExternalContactInfo contactDetail) {

        WxCpWelcomeMsg wxCpWelcomeMsg=new WxCpWelcomeMsg();
        wxCpWelcomeMsg.setWelcomeCode(callBackBaseMsg.getWelcomeCode());
        Text text = new Text();
        //是否发送默认欢迎语
        boolean sendDefaultMsg=true;

        if(StrUtil.isNotEmpty(iYqueUserCode.getWeclomeMsg())){
            text.setContent(iYqueUserCode.getWeclomeMsg());
            sendDefaultMsg=false;
        }

        //默认欢迎语
        if(sendDefaultMsg){
            IYqueDefaultMsg defaultMsg = SpringUtil.getBean(IYqueDefaultMsgService.class).findDefaultMsg();
            if(null != defaultMsg){
                text.setContent(defaultMsg.getDefaultContent());
            }
        }


        //发送欢迎语
        if(StrUtil.isNotEmpty(text.getContent())){

            //替换为真实用户名
            if(text.getContent().contains(IYqueContant.USER_NICKNAME_TPL)){
                text.setContent(
                        text.getContent().replace(IYqueContant.USER_NICKNAME_TPL,  contactDetail.getExternalContact().getName())
                );
            }

            wxCpWelcomeMsg.setText(text);
            try {
                WxCpExternalContactService externalContactService
                        = SpringUtil.getBean(IYqueConfigService.class).findWxcpservice().getExternalContactService();
                externalContactService.sendWelcomeMsg(wxCpWelcomeMsg);
            }catch (Exception e){
                 log.error("欢迎语发送失败:"+e.getMessage());
            }

        }


    }
}
