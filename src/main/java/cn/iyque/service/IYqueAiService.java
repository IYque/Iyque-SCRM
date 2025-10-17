package cn.iyque.service;

import ai.z.openapi.service.embedding.EmbeddingResponse;
import cn.iyque.exception.IYqueException;

import java.util.List;

public interface IYqueAiService {

    /**
     * 调用ai同步处理通用内容 (单条会话)
     * @param content
     * @return
     */
    String aiHandleCommonContent(String content) throws IYqueException;



    /**
     * 调用ai同步处理通用内容 (单条会话),响应json格式
     * @param content
     * @return
     */
    String aiHandleCommonContentToJson(String content) throws IYqueException;






    /**
     * 向量值计算
     * @param text
     * @return
     */
    EmbeddingResponse embedding(List<String> text);

}
