package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueDefaultMsgDao;
import cn.iyque.entity.IYqueDefaultMsg;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueDefaultMsgService;
import cn.iyque.service.IYqueMsgAnnexService;
import lombok.extern.slf4j.Slf4j;
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
