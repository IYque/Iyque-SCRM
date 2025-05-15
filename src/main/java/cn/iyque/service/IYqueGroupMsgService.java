package cn.iyque.service;

import cn.iyque.entity.IYqueGroupMsg;

public interface IYqueGroupMsgService {

    /**
     * 群发构建
     * @param iYqueGroupMsg
     */
    void buildGroupMsg(IYqueGroupMsg iYqueGroupMsg);
}
