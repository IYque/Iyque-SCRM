package cn.iyque.service;

import cn.iyque.entity.IYqueKfMsgSub;
import cn.iyque.entity.IYqueKnowledgeInfo;
import me.chanjar.weixin.cp.bean.kf.WxCpKfMsgListResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYqueKfMsgService {

    /**
     * 客户与客服会话信息入库
     * @param kfMsgId
     * @param item
     */
    void  saveIYqueKfMsg(Long kfMsgId, WxCpKfMsgListResp.WxCpKfMsgItem item);




    /**
     * 客户与客服会话列表
     * @param iYqueKfMsgSub
     * @param pageable
     * @return
     */
    Page<IYqueKfMsgSub> findAll(IYqueKfMsgSub iYqueKfMsgSub, Pageable pageable);
}
