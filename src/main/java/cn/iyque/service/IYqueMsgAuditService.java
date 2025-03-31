package cn.iyque.service;

import cn.iyque.domain.EmployeeChatGroup;
import cn.iyque.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IYqueMsgAuditService {


    /**
     * 分页获取会话列表
     * @param iYqueMsgAudit
     * @param pageable
     * @return
     */
    Page<IYqueMsgAudit> findAll(IYqueMsgAudit iYqueMsgAudit, Pageable pageable);


    /**
     * ai生成报告查询
     * @param analysisMsgAudit
     * @param pageable
     * @return
     */
    Page<IYqueAiAnalysisMsgAudit> findAiAnalysisMsgAudits(IYqueAiAnalysisMsgAudit analysisMsgAudit, Pageable pageable);

    /**
     * 同步会话数据
     */
    void synchMsg() throws Exception;


    /**
     * 数据处理,入库等处理
     * @param yqueMsgAudit
     */
    void handleMsg(IYqueMsgAudit yqueMsgAudit);


    /**
     * ai会话预警
     */
    void aISessionWarning(List<IYqueMsgRule> iYqueMsgRules, BaseEntity baseEntity );


    /**
     *  获取当天凌晨到此刻时间,员工发送给客户的聊天数据
     * @return
     */
    String findNowUserInquiryMsgData(BaseEntity baseEntity);


    /**
     * 将聊天记录按员工和客户分组，并封装到自定义数据结构中
     * @param msgAuditList 聊天记录列表
     * @return 分组后的结果
     */
     List<EmployeeChatGroup> groupByEmployeeAndCustomer(List<IYqueMsgAudit> msgAuditList);
}
