package cn.iyque.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueAgentDao;
import cn.iyque.dao.IYqueAgentSubDao;
import cn.iyque.domain.AgentMsgDto;
import cn.iyque.entity.IYqueAgent;
import cn.iyque.entity.IYqueAgentSub;
import cn.iyque.entity.IYqueRobot;
import cn.iyque.entity.IYqueRobotSub;
import cn.iyque.service.IYqueAgentService;
import cn.iyque.service.IYqueConfigService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class IYqueAgentServiceImpl implements IYqueAgentService {
    @Autowired
    IYqueAgentDao iYqueAgentDao;

    @Autowired
    IYqueAgentSubDao iYqueAgentSubDao;


    @Autowired
    IYqueConfigService iYqueConfigService;

    @Override
    public void addOrUpdate(IYqueAgent iYqueAgent) {
        iYqueAgentDao.saveAndFlush(iYqueAgent);
    }

    @Override
    public void batchDelete(Long[] ids) {
        iYqueAgentDao.deleteAllByIdInBatch(Arrays.asList(ids));
    }

    @Override
    public Page<IYqueAgent> findAll(Pageable pageable) {
        return iYqueAgentDao.findAll(pageable);
    }

    @Override
    public Page<IYqueAgentSub> findAgentSubAll(Long agentId, Pageable pageable) {
        Specification<IYqueAgentSub> spec = Specification.where(null);

        if (agentId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("agentId")), agentId));
        }
        return iYqueAgentSubDao.findAll(spec,pageable);
    }

    @Override
    public void sendAgentMsg(IYqueAgent iYqueAgent) throws Exception {
        Optional<IYqueAgent> optional = iYqueAgentDao.findById(iYqueAgent.getId());
        if(optional.isPresent()){
            List<IYqueAgentSub> iYqueAgentSubs = iYqueAgent.getIYqueAgentSub();
            if(CollectionUtil.isNotEmpty(iYqueAgentSubs)){
                IYqueAgentSub iYqueAgentSub = iYqueAgentSubs.stream().findFirst().get();
                iYqueAgentSub.setMsgTitle(iYqueAgent.getMsgTitle());
                iYqueAgentSub.setAgentId(iYqueAgent.getId());
                iYqueAgentSub.setStatus(3);
                iYqueAgentSub.prePersist(iYqueAgentSub);
                iYqueAgentSub.setSendTime(new Date());

                WxCpMessage wxCpMessage = AgentMsgDto.buildAgentMsg(iYqueAgentSub);
                if(null != wxCpMessage){
                    //全部成员
                    if(new Integer(0).equals(iYqueAgentSub.getScopeType())){
                        wxCpMessage.setToUser("@all");
                    }else{
                        if(StringUtils.isNotEmpty(iYqueAgentSub.getToUserIds())){
                            wxCpMessage.setToUser(
                                    String.join("|", iYqueAgentSub.getToUserIds().split(","))
                            );
                        }
                    }

                    wxCpMessage.setAgentId(iYqueAgent.getAgentId());

                    if(StringUtils.isNotEmpty(wxCpMessage.getToUser())&&wxCpMessage.getAgentId() != null){
                        WxCpMessageSendResult sendResult = iYqueConfigService.findWxcpservice().getMessageService().send(wxCpMessage);

                        if(StringUtils.isNotEmpty(sendResult.getMsgId())){
                            iYqueAgentSub.setMsgId(sendResult.getMsgId());
                            iYqueAgentSub.setStatus(2);
                        }
                    }
                }
                iYqueAgentSubDao.saveAll(iYqueAgentSubs);
            }

        }



    }
}
