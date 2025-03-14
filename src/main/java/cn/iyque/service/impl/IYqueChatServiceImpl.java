package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueChatDao;
import cn.iyque.dao.IYqueUserDao;
import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueUser;
import cn.iyque.service.IYqueChatService;
import cn.iyque.service.IYqueConfigService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalGroupChatInfo;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalGroupChatList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IYqueChatServiceImpl implements IYqueChatService {

    @Autowired
    private IYqueChatDao iYqueChatDao;

    @Autowired
    private IYqueUserDao iYqueUserDao;


    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Override
    public Page<IYqueChat> findAll(String name, Pageable pageable) {

        Specification<IYqueChat> spec = Specification.where(null);

        if (StringUtils.isNotEmpty(name)) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("chatName")), "%" + name.toLowerCase() + "%"));
        }

        Page<IYqueChat> iYqueChats = iYqueChatDao.findAll(spec, pageable);

        List<IYqueChat> content = iYqueChats.getContent();
        if(CollectionUtil.isNotEmpty(content)){

            List<IYqueUser> iYqueUsers = iYqueUserDao.findAll();

            content.stream().forEach(k->{


                if(CollectionUtil.isNotEmpty(iYqueUsers)){
                    List<IYqueUser> fIYqueUser = iYqueUsers.stream().filter(item -> item.getUserId().equals(k.getOwner()))
                            .collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(fIYqueUser)){
                        k.setOwnerName(
                                fIYqueUser.stream().findFirst().get().getName()
                        );

                    }

                }



            });
        }

        return iYqueChats;
    }

    @Override
    public List<IYqueChat> findAllIYqueChat() {
        return iYqueChatDao.findAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async
    public void synchIyqueChat() {

        try {
            List<IYqueChat> iYqueChats = this.listAllGroupChats();


            iYqueChatDao.deleteAll();
            iYqueChatDao.saveAllAndFlush(iYqueChats);
        }catch (Exception e){
            log.error("客群同步失败:"+e.getMessage());
        }

    }


    private  List<IYqueChat> listAllGroupChats() throws Exception {

        List<IYqueChat> iYqueChatList=new ArrayList<>();

        String cursor = null;
        int limit = 100; // 每次查询的数量

        while (true) {
            WxCpService wxCpService = iYqueConfigService.findWxcpservice();
            WxCpUserExternalGroupChatList groupChat = wxCpService.getExternalContactService().listGroupChat(limit, cursor, 0, null);

            if (groupChat == null || CollectionUtil.isEmpty(groupChat.getGroupChatList() )) {
                break;
            }
            // 处理当前页的数据
            for (WxCpUserExternalGroupChatList.ChatStatus chat : groupChat.getGroupChatList()) {
                WxCpUserExternalGroupChatInfo groupChatInfo
                        = wxCpService.getExternalContactService().getGroupChat(chat.getChatId(), 1);
                if(null != groupChatInfo){
                    WxCpUserExternalGroupChatInfo.GroupChat wGroupChat = groupChatInfo.getGroupChat();

                    if(null != wGroupChat){
                        iYqueChatList.add(
                                IYqueChat.builder()
                                        .chatId(wGroupChat.getChatId())
                                        .chatName(StringUtils.isNotEmpty(wGroupChat.getName())?wGroupChat.getName():"@微信群")
                                        .owner(wGroupChat.getOwner())
                                        .createTime(new Date(wGroupChat.getCreateTime() * 1000L))
                                        .build()
                        );
                    }


                }
            }

            // 更新cursor为下一页的cursor
            cursor = groupChat.getNextCursor();

            // 如果cursor为null，表示已经查询完所有数据
            if (cursor == null) {
                break;
            }
        }


        return iYqueChatList;

    }
}
