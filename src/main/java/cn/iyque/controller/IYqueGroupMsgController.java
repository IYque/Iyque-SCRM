package cn.iyque.controller;


import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueGroupMsg;
import cn.iyque.service.IYqueGroupMsgService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


/**
 * 群发
 */
@RestController
@RequestMapping("/groupMsg")
@Slf4j
public class IYqueGroupMsgController {

    @Autowired
    private IYqueGroupMsgService yqueGroupMsgService;


    /**
     * 获取群发列表
     * @return
     */
    @GetMapping("/findIYqueGroupMsgPage")
    public ResponseResult<IYqueGroupMsg> findIYqueGroupMsgPage(IYqueGroupMsg iYqueGroupMsg){
        Page<IYqueGroupMsg> iYqueGroupMsgs = yqueGroupMsgService.findIYqueGroupMsgPage(iYqueGroupMsg,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("updateTime").descending()));

        return new ResponseResult(iYqueGroupMsgs.getContent(),iYqueGroupMsgs.getTotalElements());

    }


    /**
     * 获取群发任务详情
     * @param id
     * @return
     */
    @GetMapping("/findIYqueGroupMsgById/{id}")
    public ResponseResult findIYqueGroupMsgById(@PathVariable Long id){
        IYqueGroupMsg iYqueGroupMsg = yqueGroupMsgService.findIYqueGroupMsgById(id);
        return new ResponseResult(iYqueGroupMsg);

    }


    /**
     * 群发任务构建
     * @param iYqueGroupMsg
     * @return
     */
    @PostMapping("/buildGroupMsg")
    public ResponseResult buildGroupMsg(@RequestBody IYqueGroupMsg iYqueGroupMsg){

        yqueGroupMsgService.buildGroupMsg(iYqueGroupMsg);

        return new ResponseResult();

    }



}
