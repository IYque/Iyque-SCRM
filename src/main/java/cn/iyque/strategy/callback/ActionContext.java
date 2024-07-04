package cn.iyque.strategy.callback;

import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueUserCode;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;

public class ActionContext {
    private ActionStrategy actionStrategy;

    public ActionContext(ActionStrategy actionStrategy) {
        this.actionStrategy = actionStrategy;
    }

    public void setActionStrategy(ActionStrategy actionStrategy) {
        this.actionStrategy = actionStrategy;
    }

    public void executeStrategy(IYqueCallBackBaseMsg callBackBaseMsg, IYqueUserCode iYqueUserCode, WxCpExternalContactInfo contactDetail) {
        actionStrategy.execute(callBackBaseMsg,iYqueUserCode,contactDetail);
    }
}
