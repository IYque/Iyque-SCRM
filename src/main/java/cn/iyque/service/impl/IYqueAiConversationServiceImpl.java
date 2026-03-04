package cn.iyque.service.impl;

import cn.iyque.entity.IYqueAiConversation;
import cn.iyque.entity.IYqueAiConversationMessage;
import cn.iyque.mapper.IYqueAiConversationMapper;
import cn.iyque.mapper.IYqueAiConversationMessageMapper;
import cn.iyque.service.IYqueAiConversationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class IYqueAiConversationServiceImpl extends ServiceImpl<IYqueAiConversationMapper, IYqueAiConversation> implements IYqueAiConversationService {

    private final IYqueAiConversationMessageMapper messageMapper;

    @Override
    public List<IYqueAiConversation> getConversationList(Long userId) {
        LambdaQueryWrapper<IYqueAiConversation> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(IYqueAiConversation::getUserId, userId);
        }
        wrapper.orderByDesc(IYqueAiConversation::getUpdateTime);
        List<IYqueAiConversation> list = list(wrapper);
        
        for (IYqueAiConversation conversation : list) {
            LambdaQueryWrapper<IYqueAiConversationMessage> msgWrapper = new LambdaQueryWrapper<>();
            msgWrapper.eq(IYqueAiConversationMessage::getConversationId, conversation.getConversationId());
            msgWrapper.orderByDesc(IYqueAiConversationMessage::getCreateTime);
            msgWrapper.last("LIMIT 1");
            IYqueAiConversationMessage lastMsg = messageMapper.selectOne(msgWrapper);
            if (lastMsg != null) {
                conversation.setLastMessage(lastMsg.getContent());
                conversation.setLastMessageTime(lastMsg.getCreateTime());
            }
        }
        
        return list;
    }

    @Override
    @Transactional
    public IYqueAiConversation createConversation(IYqueAiConversation conversation) {
        if (conversation.getConversationId() == null || conversation.getConversationId().isEmpty()) {
            conversation.setConversationId(UUID.randomUUID().toString());
        }
        conversation.setCreateTime(new Date());
        conversation.setUpdateTime(new Date());
        conversation.setDeleted(0);
        save(conversation);
        return conversation;
    }

    @Override
    @Transactional
    public IYqueAiConversation updateConversation(IYqueAiConversation conversation) {
        LambdaQueryWrapper<IYqueAiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IYqueAiConversation::getConversationId, conversation.getConversationId());
        IYqueAiConversation existing = getOne(wrapper);
        
        if (existing != null) {
            existing.setTitle(conversation.getTitle());
            existing.setModelName(conversation.getModelName());
            existing.setRole(conversation.getRole());
            existing.setTemperature(conversation.getTemperature());
            existing.setTopP(conversation.getTopP());
            existing.setMaxHistoryRounds(conversation.getMaxHistoryRounds());
            existing.setUpdateTime(new Date());
            updateById(existing);
            return existing;
        }
        
        conversation.setUpdateTime(new Date());
        updateById(conversation);
        return conversation;
    }

    @Override
    @Transactional
    public void deleteConversation(String conversationId) {
        LambdaQueryWrapper<IYqueAiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IYqueAiConversation::getConversationId, conversationId);
        remove(wrapper);

        LambdaQueryWrapper<IYqueAiConversationMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(IYqueAiConversationMessage::getConversationId, conversationId);
        messageMapper.delete(msgWrapper);
    }

    @Override
    public List<IYqueAiConversationMessage> getMessages(String conversationId) {
        LambdaQueryWrapper<IYqueAiConversationMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IYqueAiConversationMessage::getConversationId, conversationId);
        wrapper.orderByAsc(IYqueAiConversationMessage::getCreateTime);
        return messageMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void saveMessage(IYqueAiConversationMessage message) {
        message.setCreateTime(new Date());
        messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void saveMessages(String conversationId, List<IYqueAiConversationMessage> messages) {
        LambdaQueryWrapper<IYqueAiConversationMessage> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(IYqueAiConversationMessage::getConversationId, conversationId);
        messageMapper.delete(deleteWrapper);
        
        for (IYqueAiConversationMessage message : messages) {
            message.setConversationId(conversationId);
            message.setCreateTime(new Date());
            messageMapper.insert(message);
        }
    }

    @Override
    public Map<String, Object> getConversationWithMessages(String conversationId) {
        Map<String, Object> result = new HashMap<>();
        
        LambdaQueryWrapper<IYqueAiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IYqueAiConversation::getConversationId, conversationId);
        IYqueAiConversation conversation = getOne(wrapper);
        
        if (conversation != null) {
            result.put("conversation", conversation);
            result.put("messages", getMessages(conversationId));
        }
        
        return result;
    }
}
