package cn.iyque.service.impl;

import antlr.StringUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueUserCodeDao;
import cn.iyque.domain.IYqueUserCode;
import cn.iyque.entity.NewContactWay;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueUserCodeService;
import cn.iyque.utils.SnowFlakeUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpAgent;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayInfo;
import me.chanjar.weixin.cp.bean.external.WxCpContactWayResult;
import me.chanjar.weixin.cp.bean.external.WxCpGroupJoinWayInfo;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.messagebuilder.NewsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IYqueUserCodeServiceImpl implements IYqueUserCodeService {

    @Autowired
    private IYqueUserCodeDao iYqueUserCodeDao;

    @Autowired
    private IYqueConfigServiceImpl iYqueConfigService;



    @Override
    public Page<IYqueUserCode> findAll(Pageable pageable) {
        return iYqueUserCodeDao.findAll(pageable);
    }

    @Override
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
            }


        }catch (Exception e){
            throw e;
        }

    }

    @Override
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
        }catch (Exception e){

            throw e;
        }

    }

    @Override
    public IYqueUserCode findIYqueUserCodeById(Integer id) {
        return iYqueUserCodeDao.getById(id);
    }

    @Override
    public void batchDelete(Integer[] ids){
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
    public void distributeUserCode(Integer id) throws WxErrorException {
//        IYqueUserCode iYqueUserCode = findIYqueUserCodeById(id);
//        if(iYqueUserCode != null){
//            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
//            NewArticle newArticle=new NewArticle();
//            newArticle.setDescription(null);
//            newArticle.setTitle(null);
//            newArticle.setUrl(null);
//            newArticle.setPicUrl(null);
//            wxcpservice.getMessageService().send(WxCpMessage.NEWS()
//                    .toUser()
//                    .agentId()
//                    .addArticle(newArticle)
//                    .build());
//        }



    }

}
