package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueDefaultMsgDao;
import cn.iyque.dao.IYqueMsgAnnexDao;
import cn.iyque.dao.IYqueUserCodeDao;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueDefaultMsg;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueDefaultMsgService;
import cn.iyque.service.IYqueMsgAnnexService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpExternalContactService;
import me.chanjar.weixin.cp.bean.external.WxCpWelcomeMsg;
import me.chanjar.weixin.cp.bean.external.contact.WxCpExternalContactInfo;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import me.chanjar.weixin.cp.bean.external.msg.Text;
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
    IYqueUserCodeDao iYqueUserCodeDao;


    @Autowired
    IYqueConfigService iYqueConfigService;

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

    @Override
    public void sendWelcomeMsg(IYqueCallBackBaseMsg callBackBaseMsg) {

        StringBuilder tagIds=new StringBuilder();
        try {
            WxCpWelcomeMsg wxCpWelcomeMsg=new WxCpWelcomeMsg();
            wxCpWelcomeMsg.setWelcomeCode(callBackBaseMsg.getWelcomeCode());
            Text text = new Text();
            if(StrUtil.isEmpty(callBackBaseMsg.getState())){
                this.setDefaultMsg(text,wxCpWelcomeMsg);
            }else{
                IYqueUserCode iYqueUserCode = iYqueUserCodeDao.findByCodeState(callBackBaseMsg.getState());
                if(null != iYqueUserCode){
                    //标签
                    tagIds.append(iYqueUserCode.getTagId());
                    if(StrUtil.isNotEmpty(iYqueUserCode.getWeclomeMsg())){
                        text.setContent(iYqueUserCode.getWeclomeMsg());

                        List<IYqueMsgAnnex> msgAnnexes
                                = iYqueMsgAnnexService.findIYqueMsgAnnexByMsgId(iYqueUserCode.getId());
                        //设置活码欢迎语附件
                        if(CollectionUtil.isNotEmpty(msgAnnexes)){
                            wxCpWelcomeMsg.setAttachments(
                                    iYqueMsgAnnexService.msgAnnexToAttachment(msgAnnexes)
                            );
                        }
                    }else{
                        this.setDefaultMsg(text,wxCpWelcomeMsg);
                    }

                }else{
                    this.setDefaultMsg(text,wxCpWelcomeMsg);
                }
            }

            WxCpExternalContactService externalContactService
                    = iYqueConfigService.findWxcpservice().getExternalContactService();

            if(StrUtil.isNotEmpty(text.getContent())){
                //替换为真实用户名
                if(text.getContent().contains(IYqueContant.USER_NICKNAME_TPL)){
                    WxCpExternalContactInfo contactDetail = externalContactService.getContactDetail(callBackBaseMsg.getExternalUserID(), null);
                    log.info("获取客户信息:"+contactDetail);
                    if(null != contactDetail){
                        text.setContent(
                                text.getContent().replace(IYqueContant.USER_NICKNAME_TPL,  contactDetail.getExternalContact().getName())
                        );
                    }
                }

            }

            wxCpWelcomeMsg.setText(text);







            //欢迎语发送
            externalContactService.sendWelcomeMsg(wxCpWelcomeMsg);

        }catch (Exception e){
            log.error("欢迎语发送失败:"+e.getMessage());
        }finally {

            if(StrUtil.isNotEmpty(tagIds.toString())){

                try {
                    iYqueConfigService.findWxcpservice().getExternalContactService()
                            .markTag(callBackBaseMsg.getUserID(),callBackBaseMsg.getExternalUserID(),tagIds.toString().split(","),null);
                }catch (Exception e){
                    log.error("未客户打标签失败:"+e.getMessage());
                }


            }

        }

    }


    //设置全局欢迎语
    public void setDefaultMsg(Text text, WxCpWelcomeMsg wxCpWelcomeMsg){
        IYqueDefaultMsg defaultMsg = findDefaultMsg();
        if(null != defaultMsg){
            text.setContent(defaultMsg.getDefaultContent());
            List<IYqueMsgAnnex> annexLists = defaultMsg.getAnnexLists();
            //附件
            if(CollectionUtil.isNotEmpty(annexLists)){
                wxCpWelcomeMsg.setAttachments(
                        iYqueMsgAnnexService.msgAnnexToAttachment(annexLists)
                );
            }
        }
    }

}
