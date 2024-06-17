package cn.iyque.strategy.callback;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.domain.IYqueUserCode;
import cn.iyque.service.IYqueConfigService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;


/**
 * 自动打标签
 */
@Slf4j
public class MakeTagCustomerStrategy implements ActionStrategy {
    @Override
    public void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYqueUserCode iYqueUserCode, WxCpExternalContactInfo contactDetail) {
        if(null != iYqueUserCode && StrUtil.isNotEmpty(iYqueUserCode.getTagId())){
            try {
                SpringUtil.getBean(IYqueConfigService.class).findWxcpservice().getExternalContactService()
                        .markTag(callBackBaseMsg.getUserID(),callBackBaseMsg.getExternalUserID(),iYqueUserCode.getTagId().split(","),null);
            }catch (Exception e){
                log.error("未客户打标签失败:"+e.getMessage());
            }

        }

    }
}
