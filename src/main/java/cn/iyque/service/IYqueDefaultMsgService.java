package cn.iyque.service;

import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.domain.IYqueConfig;
import cn.iyque.domain.IYqueDefaultMsg;

public interface IYqueDefaultMsgService {

    IYqueDefaultMsg findDefaultMsg();



    void saveOrUpdate(IYqueDefaultMsg iYqueDefaultMsg);




}
