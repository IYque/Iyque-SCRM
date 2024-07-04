package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueDefaultMsgDao;
import cn.iyque.domain.IYqueDefaultMsg;
import cn.iyque.service.IYqueDefaultMsgService;
import cn.iyque.service.IYqueMsgAnnexService;
import cn.iyque.strategy.callback.ActionContext;
import cn.iyque.strategy.callback.MakeTagCustomerStrategy;
import cn.iyque.strategy.callback.RemarkCustomerStrategy;
import cn.iyque.strategy.callback.SendWelcomeMsgStrategy;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@Slf4j
public class IYqueDefaultMsgImpl implements IYqueDefaultMsgService {

    @Autowired
    IYqueDefaultMsgDao iYqueDefaultMsgDao;


    @Autowired
    IYqueMsgAnnexService iYqueMsgAnnexService;





    @Override
    public IYqueDefaultMsg findDefaultMsg() {
        List<IYqueDefaultMsg> iYqueDefaultMsgs = iYqueDefaultMsgDao.findAll();
        if(CollectionUtil.isEmpty(iYqueDefaultMsgs)){
            return new IYqueDefaultMsg();
        }
        IYqueDefaultMsg iYqueDefaultMsg = iYqueDefaultMsgs.stream().findFirst().get();
        iYqueDefaultMsg.setAnnexLists(
                iYqueMsgAnnexService.findIYqueMsgAnnexByMsgId(iYqueDefaultMsg.getId())
        );
        return iYqueDefaultMsg;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(IYqueDefaultMsg iYqueDefaultMsg) {
        iYqueDefaultMsgDao.saveAndFlush(iYqueDefaultMsg);
        List<IYqueMsgAnnex> annexLists = iYqueDefaultMsg.getAnnexLists();
        iYqueMsgAnnexService.deleteIYqueMsgAnnexByMsgId(iYqueDefaultMsg.getId());
        if(CollectionUtil.isNotEmpty(annexLists)){
            annexLists.stream().forEach(k->{k.setMsgId(iYqueDefaultMsg.getId());});
            iYqueMsgAnnexService.saveAll(annexLists);
        }
    }



}
