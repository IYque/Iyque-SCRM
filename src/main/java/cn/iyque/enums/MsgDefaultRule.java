package cn.iyque.enums;


import cn.iyque.entity.IYqueMsgRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
public enum MsgDefaultRule {
    A("通过不正当手段要求客户下单或完成交易。",true),

    B("使用侮辱性、攻击性语言对待客户。",true),
    C("员工向客户索要红包或其他利益。",true),
    D("故意隐瞒客户已有对接人的信息,误导客户与自己交易。",true);


    private String ruleContent;

    private Boolean status;

    MsgDefaultRule(String ruleContent,Boolean status){
        this.ruleContent=ruleContent;
        this.status=status;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


    public static  List<IYqueMsgRule> getAllRules() {
        List<IYqueMsgRule> msgRules=new ArrayList<>();
        Arrays.asList(MsgDefaultRule.values()).stream().forEach(k->{
            msgRules.add(
                    IYqueMsgRule.builder()
                            .defaultRule(true)
                            .ruleStatus(k.getStatus())
                            .createTime(new Date())
                            .ruleContent(k.getRuleContent())
                            .build()
            );


        });


        return msgRules;
    }
}
