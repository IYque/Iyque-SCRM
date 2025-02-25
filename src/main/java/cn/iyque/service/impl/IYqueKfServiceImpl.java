package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.iyque.dao.IYqueAiTokenRecordDao;
import cn.iyque.dao.IYqueKfMsgDao;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueAiTokenRecord;
import cn.iyque.entity.IYqueKfMsg;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.service.IYqueAiTokenRecordService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueKfService;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatCompletion;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatCompletionResponse;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatMessage;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.Choice;
import io.github.lnyocly.ai4j.platform.openai.usage.Usage;
import io.github.lnyocly.ai4j.service.IChatService;
import io.github.lnyocly.ai4j.service.PlatformType;
import io.github.lnyocly.ai4j.service.factor.AiService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpKfService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.kf.WxCpKfMsgListResp;
import me.chanjar.weixin.cp.bean.kf.WxCpKfMsgSendRequest;
import me.chanjar.weixin.cp.bean.kf.WxCpKfMsgSendResp;
import me.chanjar.weixin.cp.bean.kf.msg.WxCpKfTextMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class IYqueKfServiceImpl implements IYqueKfService {

    @Autowired
    private IYqueConfigService iYqueConfigService;


    @Autowired
    private AiService aiService;


    @Value("${ai.model}")
    private String model;

    @Value("${ai.limitToken}")
    private long limitToken;


    @Autowired
    private IYqueKfMsgDao kfMsgDao;


    @Autowired
    private IYqueAiTokenRecordService aiTokenRecordService;



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
                        kfMsgDao.save(
                                IYqueKfMsg.builder()
                                        .cursor(wxCpKfMsgListResp.getNextCursor())
                                        .openKfid(callBackBaseMsg.getOpenKfId())
                                        .pullTime(new Date())
                                        .build()
                        );
                    }


                    log.info("拉取的客服数据:"+wxCpKfMsgListResp);


                    if( wxCpKfMsgListResp.getHasMore().equals(new Integer(0))){

                        List<WxCpKfMsgListResp.WxCpKfMsgItem> msgList =
                                wxCpKfMsgListResp.getMsgList();
                        if(CollectionUtil.isNotEmpty(msgList)){

                            msgList.stream()
                                    .collect(Collectors.groupingBy(WxCpKfMsgListResp.WxCpKfMsgItem::getExternalUserId)).forEach((k,v)->{
                                        ThreadUtil.execute(()->{

                                                //判断消息类型
                                                WxCpKfMsgListResp.WxCpKfMsgItem lastItem
                                                        = v.get(v.size() - 1);
                                                try {
                                                    //文本类型
                                                    if(lastItem.getMsgType().equals(IYqueMsgAnnex.MsgType.MSG_TEXT)){
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

                                        });
                                    });

                        }
                    }

                }

            }
        }catch (Exception e){
            log.error("回复客户相关信息失败:"+e.getMessage());
        }
    }

    public void sendKfMsg( WxCpKfService kfService,String content,String toUser,String openKfid,boolean isAi) throws Exception {

        StringBuilder resContent=new StringBuilder();


        if(isAi){

            //校验每日token使用数量是否达上限,避免超额使用，带来不必要的消耗
            if(aiTokenRecordService.getTotalTokensToday()<=limitToken){

                // 获取chat服务实例
                IChatService chatService = aiService.getChatService(PlatformType.DEEPSEEK);

                // 构建请求参数
                ChatCompletion chatCompletion = ChatCompletion.builder()
                        .model(model)
                        .message(ChatMessage.withUser(content))
                        .build();

                ChatCompletionResponse response = chatService.chatCompletion(chatCompletion);
                List<Choice> choices = response.getChoices();
                if(CollectionUtil.isNotEmpty(choices)){
                    resContent.append(
                            choices.stream().findFirst().get().getMessage().getContent()
                                    .getText()
                    );
                }

                Usage usage = response.getUsage();
                if(null != usage){

                    aiTokenRecordService.save(
                            IYqueAiTokenRecord.builder()
                                    .completionTokens(usage.getCompletionTokens())
                                    .promptTokens(usage.getPromptTokens())
                                    .totalTokens(usage.getTotalTokens())
                                    .createTime(new Date())
                                    .aiResId(response.getId())
                                    .model(response.getModel())
                                    .build()
                    );

                }

            }else{
                resContent.append("今日ai,token资源已耗尽");
            }

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
