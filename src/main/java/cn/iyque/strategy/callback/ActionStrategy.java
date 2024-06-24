package cn.iyque.strategy.callback;

import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.domain.IYqueUserCode;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;

public interface ActionStrategy {
    void execute(IYqueCallBackBaseMsg callBackBaseMsg, IYqueUserCode iYqueUserCode, WxCpExternalContactInfo contactDetail);
}
