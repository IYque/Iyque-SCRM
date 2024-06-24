package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueUserCodeDao;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.domain.NewContactWay;
import cn.iyque.domain.IYqueConfig;
import cn.iyque.domain.IYqueUserCode;
import cn.iyque.entity.NewContactWay;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueMsgAnnexService;
import cn.iyque.service.IYqueUserCodeService;
import cn.iyque.utils.SnowFlakeUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayInfo;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IYqueUserCodeServiceImpl implements IYqueUserCodeService {

    @Autowired
    private IYqueUserCodeDao iYqueUserCodeDao;

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;



    @Override
    public Page<IYqueUserCode> findAll(Pageable pageable) {
        return iYqueUserCodeDao.findAll(pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(IYqueUserCode iYqueUserCode) throws Exception {
        try {
            iYqueUserCode.setCreateTime(new Date());
            iYqueUserCode.setUpdateTime(new Date());
            iYqueUserCode.setCodeState(CodeStateConstant.USER_CODE_STATE+ SnowFlakeUtils.nextId());

            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            WxCpContactWayInfo wxCpGroupJoinWayInfo=new WxCpContactWayInfo();
            NewContactWay contactWay=new NewContactWay();
            contactWay.setType(WxCpContactWayInfo.TYPE.MULTI);
            contactWay.setScene(WxCpContactWayInfo.SCENE.QRCODE);
            contactWay.setSkipVerify(iYqueUserCode.getSkipVerify());
            contactWay.setState(iYqueUserCode.getCodeState());
            contactWay.setUsers(ListUtil.toList(iYqueUserCode.getUserId().split(",")));
            contactWay.setIsExclusive(iYqueUserCode.getIsExclusive());
            wxCpGroupJoinWayInfo.setContactWay(contactWay);

            WxCpContactWayResult wxCpContactWayResult = wxcpservice.getExternalContactService().addContactWay(wxCpGroupJoinWayInfo);
            if(null != wxCpContactWayResult
            && StrUtil.isNotEmpty(wxCpContactWayResult.getQrCode())
            &&StrUtil.isNotEmpty(wxCpContactWayResult.getConfigId())){
                iYqueUserCode.setCodeUrl(wxCpContactWayResult.getQrCode());
                iYqueUserCode.setConfigId(wxCpContactWayResult.getConfigId());

                iYqueUserCodeDao.save(iYqueUserCode);

                List<IYqueMsgAnnex> annexLists = iYqueUserCode.getAnnexLists();
                if(CollectionUtil.isNotEmpty(annexLists)){
                    annexLists.stream().forEach(k->{
                        k.setMsgId(iYqueUserCode.getId());
                    });
                    iYqueMsgAnnexService.saveAll(annexLists);
                }

            }


        }catch (Exception e){
            throw e;
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(IYqueUserCode iYqueUserCode) throws Exception {

        try {
            IYqueUserCode oldIYqueUserCode = this.findIYqueUserCodeById(iYqueUserCode.getId());
            if(null != oldIYqueUserCode){
                iYqueUserCode.setCodeUrl(oldIYqueUserCode.getCodeUrl());
                iYqueUserCode.setCodeState(oldIYqueUserCode.getCodeState());
                iYqueUserCode.setConfigId(oldIYqueUserCode.getConfigId());

                WxCpContactWayInfo wxCpGroupJoinWayInfo=new WxCpContactWayInfo();
                NewContactWay contactWay=new NewContactWay();
                contactWay.setConfigId(oldIYqueUserCode.getConfigId());
                contactWay.setSkipVerify(iYqueUserCode.getSkipVerify());
                contactWay.setUsers(ListUtil.toList(iYqueUserCode.getUserId().split(",")));
                contactWay.setIsExclusive(iYqueUserCode.getIsExclusive());
                wxCpGroupJoinWayInfo.setContactWay(contactWay);

                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                WxCpExternalContactService externalContactService = wxcpservice.getExternalContactService();
                externalContactService.updateContactWay(wxCpGroupJoinWayInfo);
            }

            iYqueUserCode.setUpdateTime(new Date());
            iYqueUserCodeDao.saveAndFlush(iYqueUserCode);
            iYqueMsgAnnexService.deleteIYqueMsgAnnexByMsgId(iYqueUserCode.getId());
            List<IYqueMsgAnnex> annexLists = iYqueUserCode.getAnnexLists();
            if(CollectionUtil.isNotEmpty(annexLists)){
                annexLists.stream().forEach(k->{
                    k.setMsgId(iYqueUserCode.getId());
                });
                iYqueMsgAnnexService.saveAll(annexLists);
            }
        }catch (Exception e){

            throw e;
        }

    }

    @Override
    public IYqueUserCode findIYqueUserCodeById(Long id) {
        return iYqueUserCodeDao.getById(id);
    }

    @Override
    public void batchDelete(Long[] ids){
        List<IYqueUserCode> iYqueUserCodes = iYqueUserCodeDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueUserCodes)){
            iYqueUserCodes.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueConfigService.findWxcpservice().getExternalContactService().deleteContactWay(k.getConfigId());
                    iYqueUserCodeDao.saveAndFlush(k);

                }catch (Exception e){
                   log.error("活码删除失败:"+e.getMessage());
                }

            });

        }

    }

    @Override
    public void distributeUserCode(Integer id) throws Exception {

        try {
            IYqueUserCode iYqueUserCode = findIYqueUserCodeById(id);
            if(iYqueUserCode != null){
                IYqueConfig iYqueConfig = iYqueConfigService.findIYqueConfig();
                if(null != iYqueConfig){
                    WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                    NewArticle newArticle=new NewArticle();
                    newArticle.setDescription("渠道活码,点击保存即可");
                    newArticle.setTitle(iYqueUserCode.getCodeName());
                    newArticle.setUrl(iYqueUserCode.getCodeUrl());
                    newArticle.setPicUrl(iYqueUserCode.getCodeUrl());
                    wxcpservice.getMessageService().send(WxCpMessage.NEWS()
                            .toUser(iYqueUserCode.getUserId().replace(",", "|"))
                            .agentId(new Integer(iYqueConfig.getAgentId()))
                            .addArticle(newArticle)
                            .build());
                }

            }
        }catch (Exception e){
           throw e;
        }




    }

}
