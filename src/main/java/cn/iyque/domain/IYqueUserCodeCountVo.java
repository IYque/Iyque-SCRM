package cn.iyque.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工活码统计
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IYqueUserCodeCountVo {

    //新增客户数
    private long addCustomerNumber;

    //流失客户数
    private long lostCustomerNumber;
    //员工删除客户数
    private long delCustomerNumber;
    //净增客户数
    private long netGrowthCustomerNumber;

    //今日新增客户数
    private long tdAddCustomerNumber;

    //今日流失客户数
    private long tdLostCustomerNumber;

    //今日员工删除客户数
    private long tddelCustomerNumber;

    //今日净增客户数
    private long tdNetGrowthCustomerNumber;
}
