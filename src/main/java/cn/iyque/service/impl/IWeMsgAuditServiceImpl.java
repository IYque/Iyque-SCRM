package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueMsgAuditDao;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYqueMsgAudit;
import cn.iyque.service.IWeMsgAuditService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueCustomerInfoService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpMsgAuditService;
import me.chanjar.weixin.cp.bean.msgaudit.WxCpChatDatas;
import me.chanjar.weixin.cp.bean.msgaudit.WxCpChatModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 会话存档相关
 */
@Service
@Slf4j
public class IWeMsgAuditServiceImpl implements IWeMsgAuditService {

    @Autowired
    private IYqueConfigService yqueConfigService;


    @Autowired
    private IYqueMsgAuditDao yqueMsgAuditDao;

    @Autowired
    private IYqueCustomerInfoService infoService;




    /**
     * 超时时间，单位秒
     */
    private final long timeout = 5 * 60;



    @Override
    public void synchMsg(){

        try {
            WxCpMsgAuditService msgAuditService = yqueConfigService.findWxcpservice()
                    .getMsgAuditService();

            if(null != msgAuditService){
                Long dataSeq = 0L;

                IYqueMsgAudit iYqueMsgAudit = yqueMsgAuditDao.findTopByOrderByDataSeqDesc();
                if(iYqueMsgAudit != null){
                    dataSeq= iYqueMsgAudit.getDataSeq();
                }


                WxCpChatDatas chatDatas = msgAuditService.getChatDatas(dataSeq, new Long(1000), null, null, timeout);
                if(null != chatDatas){
                    List<WxCpChatDatas.WxCpChatData> chatData =
                            chatDatas.getChatData();
                    if(CollectionUtil.isNotEmpty(chatData)){
                        //解密聊天数据
                        chatData.stream().forEach(k->{
                            try {
                                log.info("解密前的信息:"+k);
                                WxCpChatModel decryptData = msgAuditService.getDecryptData(chatDatas.getSdk(), k, new Integer(1));
                                log.info("解密后的信息:"+decryptData);
                                if(null != decryptData){
                                    //目前只处理员工至客户文字类型,拉取存储
                                    if(!StringUtils.isNotEmpty(decryptData.getRoomId())){
                                        if(IYqueMsgAnnex.MsgType.MSG_TEXT.equals(decryptData.getMsgType())){
                                            String content = decryptData.getText().getContent();
                                            handleMsg(IYqueMsgAudit.builder()
                                                    .msgId(decryptData.getMsgId())
                                                    .fromId(decryptData.getFrom())
                                                    .acceptType(1)
                                                    .acceptId(Arrays.stream(decryptData.getTolist()).findFirst().get())
                                                    .msgType(decryptData.getMsgType())
                                                    .content(content)
                                                    .dataSeq(k.getSeq())
                                                    .msgTime( new Date(decryptData.getMsgTime()))
                                                    .createTime(new Date())
                                                    .build());
                                            log.info("文字类数据获取:"+content);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                log.error("聊天会话数据解密失败:"+e.getMessage());
                            }


                        });
                    }
                }
            }



        }catch (Exception e){
            log.error("拉取会话数据报错:"+e.getMessage());
        }


    }



    @Override
    public void handleMsg(IYqueMsgAudit yqueMsgAudit) {


        log.info("入库数据:"+yqueMsgAudit);
        //发送人姓名处理
        if(StringUtils.isNotEmpty(yqueMsgAudit.getFromId())){
            //表示为客户
            if(yqueMsgAudit.getFromId().startsWith("wm")||yqueMsgAudit.getFromId().startsWith("wo")){
                yqueMsgAudit.setFromName(
                        infoService.findCustomerInfoByExternalUserId(yqueMsgAudit.getFromId()).getCustomerName()
                );
            }else{ //员工名称处理



            }

        }

        //接收人姓名处理
        if(StringUtils.isNotEmpty(yqueMsgAudit.getAcceptId())){
            //表示为客户
            if(yqueMsgAudit.getAcceptId().startsWith("wm")||yqueMsgAudit.getAcceptId().startsWith("wo")){
                yqueMsgAudit.setAcceptName(
                        infoService.findCustomerInfoByExternalUserId(yqueMsgAudit.getAcceptId()).getCustomerName()
                );
            }else{//员工名称处理


            }

        }





        //聊天数据入库
        yqueMsgAuditDao.saveAndFlush(yqueMsgAudit);
    }
}
