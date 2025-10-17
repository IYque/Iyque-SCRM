package cn.iyque.service.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.embedding.EmbeddingCreateParams;
import ai.z.openapi.service.embedding.EmbeddingResponse;
import ai.z.openapi.service.model.*;
import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.entity.IYqueAiTokenRecord;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.IYqueAiTokenRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IYqueAiServiceImpl implements IYqueAiService {

    @Value("${ai.model}")
    private String model;

    @Value("${ai.limitToken}")
    private long limitToken;

    @Value("${ai.zhipu.apiKey}")
    private String apiKey;


    @Autowired
    private IYqueParamConfig yqueParamConfig;


    @Autowired
    private IYqueAiTokenRecordService aiTokenRecordService;






    @Override
    public String aiHandleCommonContent(String content) {

        StringBuilder resContent=new StringBuilder();

        try {

            if(StringUtils.isEmpty(apiKey)){
                throw new IYqueException("请配置apiKey");
            }

            //校验每日token使用数量是否达上限,避免超额使用，带来不必要的消耗
            if(aiTokenRecordService.getTotalTokensToday()<=limitToken){

                ZhipuAiClient client = ZhipuAiClient.builder()
                        .apiKey(apiKey)
                        .build();

                // 创建聊天完成请求
                ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                        .model(model)
                        .messages(Arrays.asList(
                                ChatMessage.builder()
                                        .role(ChatMessageRole.SYSTEM.value())
                                        .content("你是一个聊天会话助手。")
                                        .build(),
                                ChatMessage.builder()
                                        .role(ChatMessageRole.USER.value())
                                        .content(content)
                                        .build()
                        ))
                        .thinking(ChatThinking.builder().type("enabled").build())
                        .temperature(1.0f)
                        .build();

                // 发送请求
                ChatCompletionResponse response = client.chat().createChatCompletion(request);

                // 获取回复
                if (response.isSuccess()) {
                    Object reply = response.getData().getChoices().get(0).getMessage().getContent();
                    resContent.append(reply);


                    Usage usage = response.getData().getUsage();

                    if (null != usage) {
                        aiTokenRecordService.save(
                                IYqueAiTokenRecord.builder()
                                        .completionTokens(usage.getCompletionTokens())
                                        .promptTokens(usage.getPromptTokens())
                                        .totalTokens(usage.getTotalTokens())
                                        .createTime(new Date())
                                        .aiResId(response.getData().getId())
                                        .model(response.getData().getModel())
                                        .build()
                        );

                    } else {
                        log.error("AI响应错误: " + response.getMsg());
                    }

                }

            }else{
                throw new IYqueException("今日ai,token资源已耗尽");
            }


        }catch (Exception e){
            log.error("ai处理问题异常:"+e.getMessage());
            throw new IYqueException("ai处理问题异常:"+e.getMessage());

        }


        return resContent.toString();
    }

    @Override
    public String aiHandleCommonContentToJson(String content) throws IYqueException {
        StringBuilder resContent=new StringBuilder();

        try {

            if(StringUtils.isEmpty(apiKey)){
                throw new IYqueException("请配置apiKey");
            }

            //校验每日token使用数量是否达上限,避免超额使用，带来不必要的消耗
            if(aiTokenRecordService.getTotalTokensToday()<=limitToken){

                ZhipuAiClient client = ZhipuAiClient.builder()
                        .apiKey(apiKey)
                        .build();

                // 创建聊天完成请求
                ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                        .model(model)
                        .messages(Arrays.asList(
                                ChatMessage.builder()
                                        .role(ChatMessageRole.SYSTEM.value())
                                        .content("你是一个数据分析助手。请严格只返回JSON格式数据，不要包含任何额外的文本、说明或解释。返回的JSON必须能够被标准解析器直接解析。")
                                        .build(),
                                ChatMessage.builder()
                                        .role(ChatMessageRole.USER.value())
                                        .content(content)
                                        .build()
                        ))
                        .responseFormat(ResponseFormat.builder().type("json_object").build())
                        .thinking(ChatThinking.builder().type("enabled").build())
                        .temperature(0.3f)
                        .build();

                // 发送请求
                ChatCompletionResponse response = client.chat().createChatCompletion(request);

                // 获取回复
                if (response.isSuccess()) {
                    Object reply = response.getData().getChoices().get(0).getMessage().getContent();

                    resContent.append(reply);


                    Usage usage = response.getData().getUsage();

                    if (null != usage) {
                        aiTokenRecordService.save(
                                IYqueAiTokenRecord.builder()
                                        .completionTokens(usage.getCompletionTokens())
                                        .promptTokens(usage.getPromptTokens())
                                        .totalTokens(usage.getTotalTokens())
                                        .createTime(new Date())
                                        .aiResId(response.getData().getId())
                                        .model(response.getData().getModel())
                                        .build()
                        );
                    } else {
                        log.error("AI响应错误: " + response.getMsg());
                    }

                }

            }else{
                throw new IYqueException("今日ai,token资源已耗尽");
            }


        }catch (Exception e){
            log.error("ai处理问题异常:"+e.getMessage());
            throw new IYqueException("ai处理问题异常:"+e.getMessage());

        }


        return resContent.toString();
    }

    @Override
    public EmbeddingResponse embedding(List<String> text) {

        try {
            return ZhipuAiClient.builder()
                    .apiKey(apiKey)
                    .build()
                    .embeddings().createEmbeddings(EmbeddingCreateParams.builder()
                    .model(yqueParamConfig.getVector().getVectorModel())
                    .input(text)
                    .dimensions(yqueParamConfig.getVector().getDimension())
                    .build());
        }catch (Exception e){
            log.error("向量计算异常:"+e.getMessage());
        }

        return null;
    }




}
