package cn.iyque.service;

import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueDefaultMsg;

public interface IYqueDefaultMsgService {

    IYqueDefaultMsg findDefaultMsg();



    void saveOrUpdate(IYqueDefaultMsg iYqueDefaultMsg);



    void callBackAction( IYqueCallBackBaseMsg callBackBaseMsg);
}
