package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.entity.IYqueAiTokenRecord;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.IYqueAiTokenRecordService;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatCompletion;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatCompletionResponse;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.ChatMessage;
import io.github.lnyocly.ai4j.platform.openai.chat.entity.Choice;
import io.github.lnyocly.ai4j.platform.openai.embedding.entity.Embedding;
import io.github.lnyocly.ai4j.platform.openai.embedding.entity.EmbeddingResponse;
import io.github.lnyocly.ai4j.platform.openai.usage.Usage;
import io.github.lnyocly.ai4j.service.IChatService;
import io.github.lnyocly.ai4j.service.PlatformType;
import io.github.lnyocly.ai4j.service.factor.AiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IYqueAiServiceImpl implements IYqueAiService {

    @Value("${ai.model}")
    private String model;

    @Value("${ai.limitToken}")
    private long limitToken;

    @Value("${ai.deepseek.apiKey}")
    private String apiKey;


    @Autowired
    private AiService aiService;

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
                throw new IYqueException("今日ai,token资源已耗尽");
            }


        }catch (Exception e){
            log.error("ai处理问题异常:"+e.getMessage());
            throw new IYqueException("ai处理问题异常:"+e.getMessage());

        }


        return resContent.toString();
    }

    @Override
    public EmbeddingResponse embedding(Embedding embedding) {

        try {
           return aiService.getEmbeddingService(PlatformType.OPENAI)
                    .embedding(embedding);
        }catch (Exception e){
            log.error("向量计算异常:"+e.getMessage());
        }

        return null;
    }



}
