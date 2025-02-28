package cn.iyque.service;

import cn.iyque.entity.IYqueMsgAudit;

public interface IWeMsgAuditService {

    /**
     * 同步会话数据
     */
    void synchMsg() throws Exception;


    /**
     * 数据处理,入库等处理
     * @param yqueMsgAudit
     */
    void handleMsg(IYqueMsgAudit yqueMsgAudit);
}
