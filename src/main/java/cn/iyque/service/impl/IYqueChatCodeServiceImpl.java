package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueChatCodeDao;
import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueChatCode;
import cn.iyque.service.IYqueChatCodeService;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.SnowFlakeUtils;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.external.WxCpGroupJoinWayInfo;
import me.chanjar.weixin.cp.bean.external.WxCpGroupJoinWayResult;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalGroupChatInfo;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalGroupChatList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
public class IYqueChatCodeServiceImpl implements IYqueChatCodeService {

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueChatCodeDao iYqueChatCodeDao;


    @Override
    public Page<IYqueChatCode> findAll(Pageable pageable) {
        return iYqueChatCodeDao.findAll(pageable);
    }

    @Override
    public void createChatCode(IYqueChatCode iYqueChatCode) throws Exception {

        try {
            iYqueChatCode.setCreateTime(new Date());
            iYqueChatCode.setUpdateTime(new Date());
            iYqueChatCode.setChatCodeState(CodeStateConstant.CHAT_CODE_STATE+ SnowFlakeUtils.nextId());

            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            WxCpGroupJoinWayInfo joinWayInfo=new WxCpGroupJoinWayInfo();
            WxCpGroupJoinWayInfo.JoinWay joinWay=new WxCpGroupJoinWayInfo.JoinWay();
            joinWay.setScene(2);
            joinWay.setRemark(iYqueChatCode.getRemark());
            joinWay.setAutoCreateRoom(iYqueChatCode.getAutoCreateRoom());
            joinWay.setRoomBaseName(iYqueChatCode.getRoomBaseName());
            joinWay.setRoomBaseId(iYqueChatCode.getRoomBaseId());
            joinWay.setChatIdList( Arrays.asList(iYqueChatCode.getChatIds().split(",")));
            joinWay.setState(iYqueChatCode.getChatCodeState());
            joinWayInfo.setJoinWay(joinWay);
            WxCpGroupJoinWayResult wxCpGroupJoinWayResult = wxcpservice.getExternalContactService().addJoinWay(joinWayInfo);


            if(null != wxCpGroupJoinWayResult
                    && StrUtil.isNotEmpty(wxCpGroupJoinWayResult.getConfigId())
                   ){
                iYqueChatCode.setConfigId(wxCpGroupJoinWayResult.getConfigId());
                //获取入群二维码地址
                WxCpGroupJoinWayInfo wxCpGroupJoinWayInfo
                        = wxcpservice.getExternalContactService().getJoinWay(joinWay.getConfigId());

                if(wxCpGroupJoinWayInfo != null){
                    WxCpGroupJoinWayInfo.JoinWay joinWay1 = wxCpGroupJoinWayInfo.getJoinWay();

                    if(null != joinWay1){
                        iYqueChatCode.setChatCodeUrl(joinWay1.getQrCode());
                    }

                }



                iYqueChatCodeDao.save(iYqueChatCode);

            }
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public void updateChatCode(IYqueChatCode iYqueChatCode) throws Exception {
        IYqueChatCode oldIYqueChatCode =
                iYqueChatCodeDao.findById(iYqueChatCode.getId()).get();
        if(null != oldIYqueChatCode){


            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
            WxCpGroupJoinWayInfo joinWayInfo=new WxCpGroupJoinWayInfo();
            WxCpGroupJoinWayInfo.JoinWay joinWay=new WxCpGroupJoinWayInfo.JoinWay();
            joinWay.setConfigId(oldIYqueChatCode.getConfigId());
            joinWay.setScene(2);
            joinWay.setRemark(iYqueChatCode.getRemark());
            joinWay.setAutoCreateRoom(iYqueChatCode.getAutoCreateRoom());
            joinWay.setRoomBaseName(iYqueChatCode.getRoomBaseName());
            joinWay.setRoomBaseId(iYqueChatCode.getRoomBaseId());
            joinWay.setChatIdList( Arrays.asList(iYqueChatCode.getChatIds().split(",")));
            joinWay.setState(oldIYqueChatCode.getChatCodeState());
            joinWayInfo.setJoinWay(joinWay);
            WxCpBaseResp wxCpBaseResp = wxcpservice.getExternalContactService().updateJoinWay(joinWayInfo);

            if(null != wxCpBaseResp
                    &&wxCpBaseResp.getErrcode().equals(IYqueContant.WECHAT_API_SUCCESS)){
                oldIYqueChatCode.setChatCodeName(iYqueChatCode.getChatCodeName());
                oldIYqueChatCode.setRemark(iYqueChatCode.getRemark());
                oldIYqueChatCode.setAutoCreateRoom(iYqueChatCode.getAutoCreateRoom());
                oldIYqueChatCode.setRoomBaseId(iYqueChatCode.getRoomBaseId());
                oldIYqueChatCode.setRoomBaseName(iYqueChatCode.getRoomBaseName());
                oldIYqueChatCode.setUpdateTime(new Date());
                iYqueChatCodeDao.saveAndFlush(oldIYqueChatCode);
            }

        }


    }

    @Override
    public  List<IYqueChat> listAllGroupChats() throws Exception {

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
                                       .chatName(wGroupChat.getName())
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
