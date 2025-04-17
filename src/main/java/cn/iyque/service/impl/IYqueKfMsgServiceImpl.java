package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.dao.IYqueKfMsgSubDao;
import cn.iyque.entity.IYqueHotWord;
import cn.iyque.entity.IYqueKfMsgSub;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueKfMsgService;
import cn.iyque.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.contact.ExternalContact;
import me.chanjar.weixin.cp.bean.kf.WxCpKfCustomerBatchGetResp;
import me.chanjar.weixin.cp.bean.kf.WxCpKfMsgListResp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class IYqueKfMsgServiceImpl implements IYqueKfMsgService {

    @Autowired
    private IYqueKfMsgSubDao iYqueKfMsgSubDao;


    @Autowired
    private IYqueConfigService iYqueConfigService;


    @Autowired
    private IYqueParamConfig iYqueParamConfig;


    @Override
    @Async
    public void saveIYqueKfMsg(Long kfMsgId, WxCpKfMsgListResp.WxCpKfMsgItem item) {


        try {

            WxCpKfCustomerBatchGetResp wxCpKfCustomerBatchGetResp = iYqueConfigService.findWxcpservice().getKfService()
                    .customerBatchGet(ListUtil.toList(item.getExternalUserId()));


            if(wxCpKfCustomerBatchGetResp.success()){

                List<ExternalContact> customerList =
                        wxCpKfCustomerBatchGetResp.getCustomerList();

                if(CollectionUtil.isNotEmpty(customerList)){
                    ExternalContact externalContact = customerList.stream().findFirst().get();

                    IYqueKfMsgSub kfMsgSub = IYqueKfMsgSub.builder()
                            .kfMsgId(kfMsgId)
                            .openKfid(item.getOpenKfid())
                            .switchUserId(item.getServicerUserId())
                            .origin(item.getOrigin())
                            .msgId(item.getMsgId())
                            .externalUserId(item.getExternalUserId())
                            .nickname(externalContact.getNickname())
                            .avatar(externalContact.getAvatar())
                            .unionid(externalContact.getUnionId())
                            .gender(externalContact.getGender())
                            .sendTime(new Date(item.getSendTime() * 1000L))
                            .build();


                    if(item.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TEXT)){//文本




                        kfMsgSub.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);
                        kfMsgSub.setContent(item.getText().getContent());



                    }else if(item.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE)){ //图片

                        kfMsgSub.setMsgType(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE);

                        kfMsgSub.setContent(
                                FileUtils.mediaToSaveImg(iYqueParamConfig.getUploadDir(),
                                        iYqueConfigService.findWxcpservice().getMediaService().download(item.getImage().getMediaId())
                                        , iYqueParamConfig.getFileViewUrl())
                        );

                    }


                    iYqueKfMsgSubDao.save(kfMsgSub);

                }

            }


        }catch (Exception e){

            log.error("客服回话入库失败:"+e.getMessage());

        }



    }

    @Override
    public Page<IYqueKfMsgSub> findAll(IYqueKfMsgSub iYqueKfMsgSub, Pageable pageable) {
        Specification<IYqueKfMsgSub> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iYqueKfMsgSub.getKfName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("kfName")), "%" + iYqueKfMsgSub.getKfName().toLowerCase() + "%"));
        }

        if(StringUtils.isNotEmpty(iYqueKfMsgSub.getNickname())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nickname")), "%" + iYqueKfMsgSub.getNickname().toLowerCase() + "%"));
        }


        return iYqueKfMsgSubDao.findAll(spec,pageable);
    }
}
