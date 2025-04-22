package cn.iyque.service;

import cn.iyque.exception.IYqueException;
import io.github.lnyocly.ai4j.platform.openai.embedding.entity.Embedding;
import io.github.lnyocly.ai4j.platform.openai.embedding.entity.EmbeddingResponse;

import java.util.List;

public interface IYqueAiService {

    /**
     * 调用ai同步处理通用内容 (单条会话)
     * @param content
     * @return
     */
    String aiHandleCommonContent(String content) throws IYqueException;






    /**
     * 向量值计算
     * @param embedding
     * @return
     */
    EmbeddingResponse embedding(Embedding embedding);

}
