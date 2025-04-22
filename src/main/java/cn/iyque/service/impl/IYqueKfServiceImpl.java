package cn.iyque.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.iyque.chain.vectorstore.IYqueVectorStore;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.WxFileType;
import cn.iyque.dao.IYqueKfDao;
import cn.iyque.dao.IYqueKfMsgDao;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.*;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.*;
import cn.iyque.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.api.WxCpKfService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.kf.*;
import me.chanjar.weixin.cp.bean.kf.msg.WxCpKfResourceMsg;
import me.chanjar.weixin.cp.bean.kf.msg.WxCpKfTextMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@SuppressWarnings("all")
public class IYqueKfServiceImpl implements IYqueKfService {

    @Autowired
    private IYqueConfigService iYqueConfigService;


    @Autowired
    private IYqueAiService iYqueAiService;




    @Autowired
    private IYqueKfMsgDao kfMsgDao;


    @Autowired
    private IYqueKfDao kfDao;


    @Autowired
    private IYqueParamConfig iYqueParamConfig;


    @Autowired
    private IYqueKfMsgService yqueKfMsgService;



    @Autowired
    private IYqueVectorStore iYqueVectorStore;


    @Autowired
    private IYqueEmbeddingService yqueEmbeddingService;


    @Override
    public Page<IYqueKf> findAll(IYqueKf iYqueKf, Pageable pageable) {
        Specification<IYqueKf> spec = Specification.where(null);

        //知识库名称
        if(StringUtils.isNotEmpty(iYqueKf.getKfName())){
            spec = spec.and((root, query, cb) -> cb.like(root.get("kfName"), "%" + iYqueKf.getKfName() + "%"));
        }


        return   kfDao.findAll(spec, pageable);
    }

    @Override
    public void handleKfMsg(IYqueCallBackBaseMsg callBackBaseMsg){

        try {
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            if(null != wxcpservice){

                WxCpKfService kfService = wxcpservice.getKfService();

                if(null != kfService){

                    //欢迎语
                    if(StringUtils.isNotEmpty(callBackBaseMsg.getWelcomeCode())){

                        IYqueKf iYqueKf = kfDao.findByOpenKfid(callBackBaseMsg.getOpenKfId());
                        if(null != iYqueKf && StringUtils.isNotEmpty(iYqueKf.getWelcomeMsg())){
                            WxCpKfMsgSendRequest sendRequest=new WxCpKfMsgSendRequest();
                            sendRequest.setCode(callBackBaseMsg.getWelcomeCode());
                            sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);
                            WxCpKfTextMsg textMsg=new WxCpKfTextMsg();
                            textMsg.setContent(iYqueKf.getWelcomeMsg());
                            sendRequest.setText(textMsg);
                            //发送转接欢迎语
                            iYqueConfigService.findWxcpservice().getKfService().sendMsgOnEvent(sendRequest);
                        }

                    }



                    StringBuilder dataCursor=new StringBuilder();


                    //获取上一次的cursor
                    IYqueKfMsg kfMsg
                            = kfMsgDao.findTopByOpenKfidOrderByPullTimeDesc(callBackBaseMsg.getOpenKfId());

                    if(null != kfMsg){
                        dataCursor.append(kfMsg.getCursor());
                    }

                    //获取客服消息
                    WxCpKfMsgListResp wxCpKfMsgListResp = wxcpservice.getKfService()
                            .syncMsg(dataCursor.toString(), callBackBaseMsg.getToken(), null, null,
                                    callBackBaseMsg.getOpenKfId());

                    if(null != wxCpKfMsgListResp){
                        kfMsg=IYqueKfMsg.builder()
                                .cursor(wxCpKfMsgListResp.getNextCursor())
                                .openKfid(callBackBaseMsg.getOpenKfId())
                                .pullTime(new Date())
                                .build();
                        kfMsgDao.save(
                                kfMsg
                        );
                    }


                    log.info("拉取的客服数据:"+wxCpKfMsgListResp);


                    if( wxCpKfMsgListResp.getHasMore().equals(new Integer(0))){

                        List<WxCpKfMsgListResp.WxCpKfMsgItem> msgList =
                                wxCpKfMsgListResp.getMsgList();
                        if(CollectionUtil.isNotEmpty(msgList)){
                            Map<String, List<WxCpKfMsgListResp.WxCpKfMsgItem>> listMap = msgList.stream()
                                    .collect(Collectors.groupingBy(WxCpKfMsgListResp.WxCpKfMsgItem::getExternalUserId));
                            for (String k : listMap.keySet()) {


                                //判断消息类型
                                WxCpKfMsgListResp.WxCpKfMsgItem lastItem
                                        =  listMap.get(k).get( listMap.get(k).size() - 1);

                                yqueKfMsgService.saveIYqueKfMsg(kfMsg.getId(),lastItem);
                                try {
                                    //文本类型
                                    if(lastItem.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TEXT)){
                                        IYqueKf iYqueKf = kfDao.findByOpenKfid(callBackBaseMsg.getOpenKfId());


                                        if(null != iYqueKf){
                                            if(StringUtils.isNotEmpty(iYqueKf.getKId())){

                                                //向量库检索相关数据
                                                List<String> nearest = iYqueVectorStore
                                                        .nearest(yqueEmbeddingService.getQueryVector(lastItem.getText().getContent(), callBackBaseMsg.getOpenKfId()), callBackBaseMsg.getOpenKfId());

                                                if(CollUtil.isNotEmpty(nearest)){

                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append("问题：").append(k).append("\n\n");
                                                    sb.append("参考内容：\n");


                                                    nearest.stream().forEach(kk->{
                                                        sb.append("- ").append(kk);
                                                    });

                                                    sb.append("请根据以上问题和参考内容，生成简洁准确的回答。\n");
                                                    sb.append("要求：\n");
                                                    sb.append("1. 须严格根据我给你的系统上下文内容原文进行回答；\n");
                                                    sb.append("2. 请不要自己发挥,回答时保持原来文本的段落层级；\n");
                                                    sb.append("3. 如果没有用户需要的相关内容，则返回无相关内容。");


                                                    this.sendAiKfMsg(kfService,sb.toString()
                                                            ,k, callBackBaseMsg.getOpenKfId(),true);


                                                }else{//为空则，按照客服设定的规则响应


                                                    switch (iYqueKf.getSwitchType()){
                                                        case 1://文字
                                                            this.sendKfMsg(IYqueMsgAnnex.MsgType.MSG_TEXT,kfService,iYqueKf.getSwitchText(),k, callBackBaseMsg.getOpenKfId());
                                                            break;
                                                        case 2://转人工回复
                                                            this.kfTransferPersonnel(callBackBaseMsg.getOpenKfId(),  lastItem.getExternalUserId(),iYqueKf.getSwitchUserIds(),iYqueKf.getSwitchUserNames());
                                                            break;
                                                        case 3: //发布外部联系人二维码
                                                            this.sendKfMsg(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE,kfService,iYqueKf.getSwichQrUrl(),k, callBackBaseMsg.getOpenKfId());
                                                            break;
                                                        case 4: //ai大模型直接回复
                                                            this.sendAiKfMsg(kfService,lastItem.getText().getContent(),k, callBackBaseMsg.getOpenKfId(),true);
                                                            break;
                                                    }



                                                }


                                            }
                                        }



                                        log.info("文本类型客户消息:"+lastItem.getText().getContent());
                                        //非文本类型基于提示
                                    }else {
                                        this.sendAiKfMsg(kfService,"不支持当前类型消息,请发送文字消息。",k, callBackBaseMsg.getOpenKfId(),false);

                                        log.info("其他类型文本消息:"+lastItem);
                                    }
                                }catch (Exception e){

                                    log.error("消息发送失败:"+e.getMessage());

                                }

                            }


                        }
                    }

                }

            }
        }catch (Exception e){
            log.error("回复客户相关信息失败:"+e.getMessage());
        }
    }

    @Override
    public void saveOrUpdateKf(IYqueKf iYqueKf) {


        try {

            //上传头像获取头像临时素材id
            WxMediaUploadResult uploadResult = iYqueConfigService.findWxcpservice().getMediaService().upload(WxFileType.IMAGE, FileUtils.downloadImage(
                    iYqueKf.getKfPicUrl()
            ));


            if(StringUtils.isEmpty(uploadResult.getMediaId())){
                throw new IYqueException("头像上传失败");
            }


            if(iYqueKf.getId() == null){//新建


                    //构建客服账号
                    WxCpKfAccountAdd accountAdd=new WxCpKfAccountAdd();
                    accountAdd.setMediaId(uploadResult.getMediaId());
                    accountAdd.setName(iYqueKf.getKfName());
                    WxCpKfAccountAddResp wxCpKfAccountAddResp = iYqueConfigService.findWxcpservice().getKfService().addAccount(accountAdd);
                    if(StringUtils.isEmpty(wxCpKfAccountAddResp.getOpenKfid())){
                        throw new IYqueException("客服创建失败");
                    }
                    iYqueKf.setOpenKfid(wxCpKfAccountAddResp.getOpenKfid());


                  //添加接待人员
                 if(iYqueKf.getSwitchType().equals(new Integer(2))&&StringUtils.isNotEmpty(iYqueKf.getSwitchUserIds())){
                     iYqueConfigService.findWxcpservice().getKfService().addServicer(wxCpKfAccountAddResp.getOpenKfid(),
                            Arrays.asList(iYqueKf.getSwitchUserIds().split(",")));
                 }

                //获取客服链接
                WxCpKfAccountLink accountLink=new WxCpKfAccountLink();
                accountLink.setOpenKfid(iYqueKf.getOpenKfid());
                WxCpKfAccountLinkResp accountLinkResp = iYqueConfigService.findWxcpservice().getKfService().getAccountLink(accountLink);
                if(StringUtils.isNotEmpty(accountLinkResp.getUrl())){
                    iYqueKf.setKfUrl(accountLinkResp.getUrl());
                    //客服链接转二维码
                    iYqueKf.setKfUrl(
                            FileUtils.generateQRCode(accountLinkResp.getUrl(),iYqueParamConfig.getUploadDir())
                    );

                }

                //存储入库
                kfDao.save(iYqueKf);
            }else{//更新

                //更新客服账号
                if(StringUtils.isNotEmpty(iYqueKf.getOpenKfid())&&StringUtils.isNotEmpty(iYqueKf.getKfUrl())){
                    WxCpKfAccountUpd cpKfAccountUpd=new WxCpKfAccountUpd();
                    cpKfAccountUpd.setMediaId(uploadResult.getMediaId());
                    cpKfAccountUpd.setName(iYqueKf.getKfName());
                    cpKfAccountUpd.setOpenKfid(iYqueKf.getOpenKfid());
                    WxCpBaseResp wxCpBaseResp = iYqueConfigService.findWxcpservice().getKfService().updAccount(cpKfAccountUpd);
                    if(!wxCpBaseResp.success()){
                        throw new IYqueException("客服更新失败");
                    }

                }

                //删除接待人员
                iYqueConfigService.findWxcpservice().getKfService().delServicer(iYqueKf.getOpenKfid(), Arrays.asList(iYqueKf.getSwitchUserIds().split(",")));

                //重新添加接待人员
                if(iYqueKf.getSwitchType().equals(new Integer(2))&&StringUtils.isNotEmpty(iYqueKf.getSwitchUserIds())){
                    iYqueConfigService.findWxcpservice().getKfService().addServicer(iYqueKf.getOpenKfid(),
                            Arrays.asList(iYqueKf.getSwitchUserIds().split(",")));
                }


                kfDao.saveAndFlush(iYqueKf);

            }
        }catch (Exception e){
            log.error("客户编辑或更新异常:"+e.getMessage());
            throw new IYqueException(e.getMessage());
        }



    }

    @Override
    public void batchDelete(List<Long> ids) {

        if(CollectionUtil.isNotEmpty(ids)){

            List<IYqueKf> iYqueKfs = kfDao.findAllById(ids);

            if(CollectionUtil.isNotEmpty(iYqueKfs)){
                iYqueKfs.stream().forEach(k->{

                    try {
                        WxCpKfAccountDel accountDel=new WxCpKfAccountDel();
                        accountDel.setOpenKfid(k.getOpenKfid());
                        WxCpBaseResp wxCpBaseResp = iYqueConfigService.findWxcpservice().getKfService().delAccount(accountDel);
                        if(wxCpBaseResp.success()){
                            kfDao.deleteById(k.getId());
                        }
                    }catch (Exception e){
                        log.error("客服删除失败:"+e.getMessage());
                    }

                });
            }

        }

    }


    /**
     * ai输入单条msg,回复
     * @param kfService
     * @param content
     * @param toUser
     * @param openKfid
     * @param isAi
     * @throws Exception
     */
    public void sendAiKfMsg( WxCpKfService kfService,String content,String toUser,String openKfid,boolean isAi) throws Exception {


        StringBuilder resContent=new StringBuilder();


        if(isAi){

            resContent.append(
                    iYqueAiService.aiHandleCommonContent(content)
            );

            log.info("ai大模型处理后回复的内容:"+resContent.toString());

        }else{

            resContent.append(content);
        }



        this.sendKfMsg(
                IYqueMsgAnnex.MsgType.MSG_TEXT,kfService,resContent.toString(),toUser,openKfid
        );


    }


    /**
     * 客服转接人工
     * @param openKfid 客服id
     * @param externalUserid 客服id
     * @param servicerUserid 接待人员id
     * @param servicerUserName 接待人员名称
     */
    public void kfTransferPersonnel(String openKfid,String externalUserid,String servicerUserid,String servicerUserName){

        try {

            WxCpKfServiceStateTransResp transResp
                    = iYqueConfigService.findWxcpservice().getKfService().transServiceState(openKfid, externalUserid, new Integer(3), servicerUserid);
            if(transResp.success()){
                if(StringUtils.isNotEmpty(transResp.getMsgCode())){
                    WxCpKfMsgSendRequest sendRequest=new WxCpKfMsgSendRequest();
                    sendRequest.setCode(transResp.getMsgCode());
                    sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);

                    WxCpKfTextMsg textMsg=new WxCpKfTextMsg();
                    textMsg.setContent("当前信息AI客服无法处理,已转接人工客服["+servicerUserName+"],为您服务");
                    sendRequest.setText(textMsg);

                    //发送转接欢迎语
                    iYqueConfigService.findWxcpservice().getKfService().sendMsgOnEvent(sendRequest);

                }
            }

        }catch (Exception e){

            log.error("客服转接人工失败:"+e.getMessage());
        }

    }


    /**
     * 发送客服消息
     * @param msgType
     * @param kfService
     * @param content
     * @param toUser
     * @param openKfid
     * @throws Exception
     */
    private void sendKfMsg(String msgType,WxCpKfService kfService,String content,String toUser,String openKfid)throws Exception {

        WxCpKfMsgSendRequest sendRequest=new WxCpKfMsgSendRequest();
        sendRequest.setToUser(toUser);
        sendRequest.setOpenKfid(openKfid);

        //发送文本内容
        if(msgType.equals(IYqueMsgAnnex.MsgType.MSG_TEXT)){
            sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);
            WxCpKfTextMsg textMsg=new WxCpKfTextMsg();
            textMsg.setContent(content);
            sendRequest.setText(textMsg);
        //发送图片内容
        }else if(msgType.equals(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE)){

            sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TYPE_IMAGE);
            WxCpKfResourceMsg resourceMsg=new WxCpKfResourceMsg();

            WxMediaUploadResult uploadResult = iYqueConfigService.findWxcpservice().getMediaService().upload(WxFileType.IMAGE, FileUtils.downloadImage(
                    content
            ));


            if(StringUtils.isEmpty(uploadResult.getMediaId())){
                throw new IYqueException("回复内容上传失败");
            }
            resourceMsg.setMediaId(uploadResult.getMediaId());

            sendRequest.setImage(resourceMsg);
        }




        WxCpKfMsgSendResp wxCpKfMsgSendResp = kfService.sendMsg(sendRequest);
        log.info("客服消息发送:"+wxCpKfMsgSendResp);
    }


    /**
     * ai输入多天msg,回复
     * @param kfService
     * @param contents
     * @param toUser
     * @param openKfid
     * @param isAi
     * @throws Exception
     */
    public void sendBatchAIKfMsg( WxCpKfService kfService,List<String> contents,String toUser,String openKfid,boolean isAi) throws Exception {

        StringBuilder resContent=new StringBuilder();


        if(isAi){

            resContent.append(
                    iYqueAiService.aiHandleCommonContent(contents)
            );

            log.info("ai大模型处理后回复的内容:"+resContent.toString());

        }
        this.sendKfMsg(
                IYqueMsgAnnex.MsgType.MSG_TEXT,kfService,resContent.toString(),toUser,openKfid
        );




    }

}
