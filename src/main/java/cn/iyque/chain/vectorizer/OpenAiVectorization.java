package cn.iyque.chain.vectorizer;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.service.IYqueAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.github.lnyocly.ai4j.platform.openai.embedding.entity.Embedding;
import io.github.lnyocly.ai4j.platform.openai.embedding.entity.EmbeddingResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class OpenAiVectorization implements Vectorization{



    @Autowired
    private IYqueAiService yqueAiService;


    @Autowired
    private IYqueParamConfig yqueParamConfig;





    @Override
    public List<List<Float>> batchVectorization(List<String> chunkList, String kid) {
        List<List<Float>> vectorList;


        Embedding embedding = buildEmbedding(chunkList);
        EmbeddingResponse embeddings =yqueAiService.embedding(embedding);

        // 处理 OpenAI 返回的嵌入数据
        vectorList = processOpenAiEmbeddings(embeddings);

        return vectorList;
    }

    /**
     * 构建 Embedding 对象
     */
    private Embedding buildEmbedding(List<String> chunkList) {

        return Embedding.builder()
                .input(chunkList)
                .model(yqueParamConfig.getVector().getVectorModel())
                .build();
    }

    /**
     * 处理 OpenAI 返回的嵌入数据
     */
    private List<List<Float>> processOpenAiEmbeddings(EmbeddingResponse embeddings) {
        List<List<Float>> vectorList = new ArrayList<>();

        embeddings.getData().forEach(data -> {
            List<Float> vector = data.getEmbedding();
            vectorList.add(vector);
        });

        return vectorList;
    }


//    /**
//     * 将 BigDecimal 转换为 Double 列表
//     */
//    private List<Double> convertToDoubleList(List<Float> vector) {
//        return vector.stream()
//                .map(Float::doubleValue)
//                .collect(Collectors.toList());
//    }



    @Override
    public List<Float> singleVectorization(String chunk, String kid) {
        List<String> chunkList = new ArrayList<>();
        chunkList.add(chunk);
        List<List<Float>> vectorList = batchVectorization(chunkList, kid);
        return vectorList.get(0);
    }
}
