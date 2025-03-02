package cn.iyque.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueAiAnalysisMsgAudit;
import cn.iyque.entity.IYqueMsgAudit;
import cn.iyque.service.IWeMsgAuditService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
@Slf4j
public class IYqueWeMsgAuditController {



    @Autowired
    private IWeMsgAuditService weMsgAuditService;



    /**
     * 获取会话列表
     * @param iYqueMsgAudit
     * @return
     */
    @GetMapping("/findMsgAuditByPage")
    public ResponseResult<IYqueMsgAudit> findMsgAuditByPage(IYqueMsgAudit iYqueMsgAudit){

        Page<IYqueMsgAudit> iYqueMsgAudits = weMsgAuditService.findAll(iYqueMsgAudit,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("msgTime").descending()));
        return new ResponseResult(iYqueMsgAudits.getContent(),iYqueMsgAudits.getTotalElements());
    }


    /**
     * 会话同步
     * @return
     */
    @GetMapping("/synchMsg")
    public ResponseResult synchMsg(){
        ThreadUtil.execute(()->{
            try {
                weMsgAuditService.synchMsg();
            }catch (Exception e){
               log.error("会话同步失败:"+e.getMessage());
            }
         });


        return new ResponseResult("当前会话记录正在同步中,请稍后查看");
    }




    /**
     * ai智能分析生成员工聊天是否违规记录
     */
    @GetMapping("/buildAISessionWarning")
    public ResponseResult buildAISessionWarning(){
        weMsgAuditService.aISessionWarning();
        return new ResponseResult("当前记录正在生成中,请稍后查看");
    }


    /**
     * ai分析预审报告列表
     * @param analysisMsgAudit
     * @return
     */
    @GetMapping("/findAiAnalysisMsgAudits")
    public ResponseResult<IYqueAiAnalysisMsgAudit> findAiAnalysisMsgAudits(IYqueAiAnalysisMsgAudit analysisMsgAudit){

        Page<IYqueAiAnalysisMsgAudit> msgAudits = weMsgAuditService.findAiAnalysisMsgAudits(analysisMsgAudit,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));

        return new ResponseResult(msgAudits.getContent(),msgAudits.getTotalElements());
    }


}
