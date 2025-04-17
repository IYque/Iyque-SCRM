package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.WxFileType;
import cn.iyque.dao.IYqueKfDao;
import cn.iyque.dao.IYqueKfMsgDao;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.*;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueKfMsgService;
import cn.iyque.service.IYqueKfService;
import cn.iyque.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.api.WxCpKfService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.kf.*;
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
                                        //拓展从知识库中获取相关数据
                                        this.sendKfMsg(kfService,lastItem.getText().getContent(),k, callBackBaseMsg.getOpenKfId(),true);

                                        log.info("文本类型客户消息:"+lastItem.getText().getContent());
                                        //非文本类型基于提示
                                    }else {
                                        this.sendKfMsg(kfService,"不支持当前类型消息,请发送文字消息。",k, callBackBaseMsg.getOpenKfId(),false);

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

    public void sendKfMsg( WxCpKfService kfService,String content,String toUser,String openKfid,boolean isAi) throws Exception {

        StringBuilder resContent=new StringBuilder();


        if(isAi){

            resContent.append(
                    iYqueAiService.aiHandleCommonContent(content)
            );

        }else{

            resContent.append(content);
        }

        WxCpKfMsgSendRequest sendRequest=new WxCpKfMsgSendRequest();
        sendRequest.setToUser(toUser);
        sendRequest.setOpenKfid(openKfid);
        sendRequest.setMsgType(IYqueMsgAnnex.MsgType.MSG_TEXT);
        WxCpKfTextMsg textMsg=new WxCpKfTextMsg();
        textMsg.setContent(resContent.toString());
        sendRequest.setText(textMsg);
        WxCpKfMsgSendResp wxCpKfMsgSendResp = kfService.sendMsg(sendRequest);
        log.info("客服消息发送:"+wxCpKfMsgSendResp);




    }

}
