package cn.iyque.controller;

import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueChat;
import cn.iyque.entity.IYqueUser;
import cn.iyque.service.IYqueChatService;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iYqueChat")
public class IYqueChatController {

    @Autowired
    private IYqueChatService iYqueChatService;




    /**
     * 获取群列表
     * @param iYqueChat
     * @return
     */
    @GetMapping("/findIYqueChatPage")
    public ResponseResult<IYqueChat> findIYqueChatPage(IYqueChat iYqueChat){
        Page<IYqueChat> iYqueChats = iYqueChatService.findAll(iYqueChat.getChatName(), PageRequest.of(
                TableSupport.buildPageRequest().getPageNum(), TableSupport.buildPageRequest().getPageSize()));
        return new ResponseResult(iYqueChats.getContent(),iYqueChats.getTotalElements());
    }

    /**
     * 同步客群
     * @return
     */
    @PostMapping("/synchIyqueChat")
    public ResponseResult synchIyqueChat(){

        iYqueChatService.synchIyqueChat();

        return new ResponseResult("客群同步中,请稍后查看");
    }


}
