package cn.iyque.service;

import cn.iyque.entity.IYqueAiConversation;
import cn.iyque.entity.IYqueAiConversationMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IYqueAiConversationService extends IService<IYqueAiConversation> {

    List<IYqueAiConversation> getConversationList(Long userId);

    IYqueAiConversation createConversation(IYqueAiConversation conversation);

    IYqueAiConversation updateConversation(IYqueAiConversation conversation);

    void deleteConversation(String conversationId);

    List<IYqueAiConversationMessage> getMessages(String conversationId);

    void saveMessage(IYqueAiConversationMessage message);

    void saveMessages(String conversationId, List<IYqueAiConversationMessage> messages);

    Map<String, Object> getConversationWithMessages(String conversationId);
}
