package cn.iyque.chain.split;


import io.github.lnyocly.ai4j.utils.RecursiveCharacterTextSplitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
@Primary
public class CharacterTextSplitter implements TextSplitter {



    @Override
    public List<String> split(String content) {

        RecursiveCharacterTextSplitter recursiveCharacterTextSplitter = new RecursiveCharacterTextSplitter(1000, 200);
        return recursiveCharacterTextSplitter.splitText(content);

    }
}

