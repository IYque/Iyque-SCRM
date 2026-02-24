package cn.iyque.service.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.embedding.EmbeddingCreateParams;
import ai.z.openapi.service.embedding.EmbeddingResponse;
import ai.z.openapi.service.model.*;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.domain.AiGenerateTagsResponse;
import cn.iyque.entity.IYqueAiTokenRecord;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.IYqueAiTokenRecordService;
import cn.iyque.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
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
    public List<AiGenerateTagsResponse> generateTags(String prompt, Integer groupCount, Integer tagCountPerGroup) {
        List<AiGenerateTagsResponse> result = new ArrayList<>();
        
        // 设置默认值
        if (groupCount == null || groupCount < 1) {
            groupCount = 2;
        }
        if (tagCountPerGroup == null || tagCountPerGroup < 1) {
            tagCountPerGroup = 3;
        }
        // 限制最大值
        if (groupCount > 100) {
            groupCount = 100;
        }
        if (tagCountPerGroup > 100) {
            tagCountPerGroup = 100;
        }
        
        try {
            // 构建AI提示词，指导AI生成符合格式的标签
            String aiPrompt = "请根据用户需求生成相关的标签组和标签。\n" +
                    "用户需求：" + prompt + "\n" +
                    "请按照以下JSON格式输出结果：\n" +
                    "{\n" +
                    "  \"tagGroups\": [\n" +
                    "    {\n" +
                    "      \"groupName\": \"标签组名称\",\n" +
                    "      \"tags\": [\n" +
                    "        {\"name\": \"标签1\"},\n" +
                    "        {\"name\": \"标签2\"},\n" +
                    "        {\"name\": \"标签3\"}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n" +
                    "要求：\n" +
                    "1. 生成" + groupCount + "个标签组，每个标签组包含" + tagCountPerGroup + "个标签\n" +
                    "2. 标签组名称要清晰表达标签的分类逻辑\n" +
                    "3. 标签名称要具体、有实际业务意义\n" +
                    "4. 严格按照指定的JSON格式输出，不要包含任何额外的文本\n" +
                    "5. 确保JSON格式正确，能够被标准JSON解析器解析\n";
            
            // 调用AI服务生成标签
            String aiResponse = aiHandleCommonContentToJson(aiPrompt);
            
            // 解析AI的JSON输出
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(aiResponse);
            com.fasterxml.jackson.databind.JsonNode tagGroupsNode = rootNode.get("tagGroups");
            
            if (tagGroupsNode != null && tagGroupsNode.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode groupNode : tagGroupsNode) {
                    AiGenerateTagsResponse tagGroup = new AiGenerateTagsResponse();
                    
                    // 解析标签组名称
                    com.fasterxml.jackson.databind.JsonNode groupNameNode = groupNode.get("groupName");
                    if (groupNameNode != null && groupNameNode.isTextual()) {
                        tagGroup.setGroupName(groupNameNode.asText());
                    } else {
                        continue;
                    }
                    
                    // 解析标签列表
                    List<AiGenerateTagsResponse.TagItem> tags = new ArrayList<>();
                    com.fasterxml.jackson.databind.JsonNode tagsNode = groupNode.get("tags");
                    if (tagsNode != null && tagsNode.isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode tagNode : tagsNode) {
                            com.fasterxml.jackson.databind.JsonNode tagNameNode = tagNode.get("name");
                            if (tagNameNode != null && tagNameNode.isTextual()) {
                                AiGenerateTagsResponse.TagItem tagItem = new AiGenerateTagsResponse.TagItem();
                                tagItem.setName(tagNameNode.asText());
                                tags.add(tagItem);
                            }
                        }
                    }
                    
                    if (!tags.isEmpty()) {
                        tagGroup.setTags(tags);
                        result.add(tagGroup);
                    }
                }
            }
            
            // 如果AI生成的结果为空，抛出异常
            if (result.isEmpty()) {
                throw new IYqueException("AI生成标签失败：未生成有效的标签组");
            }
            
        } catch (IYqueException e) {
            log.error("AI生成标签失败: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("AI生成标签失败: " + e.getMessage());
            throw new IYqueException("AI生成标签失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取默认标签组（当AI生成失败时使用）
     */
    private List<AiGenerateTagsResponse> getDefaultTagGroups() {
        List<AiGenerateTagsResponse> result = new ArrayList<>();
        
        // 默认标签组1：客户价值
        AiGenerateTagsResponse group1 = new AiGenerateTagsResponse();
        group1.setGroupName("客户价值");
        List<AiGenerateTagsResponse.TagItem> tags1 = new ArrayList<>();
        AiGenerateTagsResponse.TagItem tag1_1 = new AiGenerateTagsResponse.TagItem();
        tag1_1.setName("高价值客户");
        tags1.add(tag1_1);
        AiGenerateTagsResponse.TagItem tag1_2 = new AiGenerateTagsResponse.TagItem();
        tag1_2.setName("中等价值客户");
        tags1.add(tag1_2);
        AiGenerateTagsResponse.TagItem tag1_3 = new AiGenerateTagsResponse.TagItem();
        tag1_3.setName("低价值客户");
        tags1.add(tag1_3);
        group1.setTags(tags1);
        result.add(group1);
        
        // 默认标签组2：客户活跃度
        AiGenerateTagsResponse group2 = new AiGenerateTagsResponse();
        group2.setGroupName("客户活跃度");
        List<AiGenerateTagsResponse.TagItem> tags2 = new ArrayList<>();
        AiGenerateTagsResponse.TagItem tag2_1 = new AiGenerateTagsResponse.TagItem();
        tag2_1.setName("活跃客户");
        tags2.add(tag2_1);
        AiGenerateTagsResponse.TagItem tag2_2 = new AiGenerateTagsResponse.TagItem();
        tag2_2.setName("沉默客户");
        tags2.add(tag2_2);
        AiGenerateTagsResponse.TagItem tag2_3 = new AiGenerateTagsResponse.TagItem();
        tag2_3.setName("流失客户");
        tags2.add(tag2_3);
        group2.setTags(tags2);
        result.add(group2);
        
        return result;
    }

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

