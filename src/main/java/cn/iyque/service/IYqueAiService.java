package cn.iyque.service;

import ai.z.openapi.service.embedding.EmbeddingResponse;
import cn.iyque.domain.AiGenerateTagsResponse;
import cn.iyque.exception.IYqueException;

import java.util.List;

public interface IYqueAiService {
    /**
     * AI生成标签
     * @param prompt 提示词
     * @param groupCount 标签组数量
     * @param tagCountPerGroup 每组标签数量
     * @return 标签组列表
     */
    List<AiGenerateTagsResponse> generateTags(String prompt, Integer groupCount, Integer tagCountPerGroup);
    
    /**
     * 文本向量化
     * @param texts
     * @return
     */
    EmbeddingResponse embedding(List<String> texts);


    String aiHandleCommonContentToJson(String content) throws IYqueException;



    String aiHandleCommonContent(String content);


}
