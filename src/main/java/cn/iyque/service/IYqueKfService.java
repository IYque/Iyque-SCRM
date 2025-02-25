package cn.iyque.service;

import cn.iyque.domain.IYqueCallBackBaseMsg;

public interface IYqueKfService {


    /**
     * 处理回调的客服信息
     * @param callBackBaseMsg
     */
    void handleKfMsg( IYqueCallBackBaseMsg callBackBaseMsg) throws Exception;


}
