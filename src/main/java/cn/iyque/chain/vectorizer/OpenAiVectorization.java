package cn.iyque.chain.vectorizer;


import cn.iyque.entity.IYqueKnowledgeInfo;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.IYqueKnowledgeInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import io.github.lnyocly.ai4j.platform.openai.embedding.entity.Embedding;
import io.github.lnyocly.ai4j.platform.openai.embedding.entity.EmbeddingResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class OpenAiVectorization implements Vectorization{

    @Lazy
    @Autowired
    private IYqueKnowledgeInfoService knowledgeInfoService;


    @Autowired
    private IYqueAiService yqueAiService;



    @Override
    public List<List<Double>> batchVectorization(List<String> chunkList, String kid) {
        List<List<Double>> vectorList;

        // 获取知识库信息
        IYqueKnowledgeInfo knowledgeInfoVo = knowledgeInfoService.findKnowledgeInfoById(Long.valueOf(kid));

        Embedding embedding = buildEmbedding(chunkList, knowledgeInfoVo);
        EmbeddingResponse embeddings =yqueAiService.embedding(embedding);

        // 处理 OpenAI 返回的嵌入数据
        vectorList = processOpenAiEmbeddings(embeddings);

        return vectorList;
    }

    /**
     * 构建 Embedding 对象
     */
    private Embedding buildEmbedding(List<String> chunkList, IYqueKnowledgeInfo knowledgeInfoVo) {

        return Embedding.builder()
                .input(chunkList)
                .model(knowledgeInfoVo.getVectorModel())
                .build();
    }

    /**
     * 处理 OpenAI 返回的嵌入数据
     */
    private List<List<Double>> processOpenAiEmbeddings(EmbeddingResponse embeddings) {
        List<List<Double>> vectorList = new ArrayList<>();

        embeddings.getData().forEach(data -> {
            List<Float> vector = data.getEmbedding();
            List<Double> doubleVector = convertToDoubleList(vector);
            vectorList.add(doubleVector);
        });

        return vectorList;
    }


    /**
     * 将 BigDecimal 转换为 Double 列表
     */
    private List<Double> convertToDoubleList(List<Float> vector) {
        return vector.stream()
                .map(Float::doubleValue)
                .collect(Collectors.toList());
    }



    @Override
    public List<Double> singleVectorization(String chunk, String kid) {
        List<String> chunkList = new ArrayList<>();
        chunkList.add(chunk);
        List<List<Double>> vectorList = batchVectorization(chunkList, kid);
        return vectorList.get(0);
    }
}
