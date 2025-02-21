package cn.iyque.constant;

import java.util.Date;

public class IYqueContant {

    //正常状态
    public static final Integer COMMON_STATE=0;

    //删除状态
    public static final Integer DEL_STATE=1;

    //用户昵称模版
    public static final String USER_NICKNAME_TPL="$客户昵称$";



    //企微api成功返回状态码
    public static final Integer WECHAT_API_SUCCESS=0;



    //投诉通知模版
    public static final String complaintTipTpl= "<div class=\"gray\">%s</div> <div class=\"normal\">投诉类型:%s，联系方式:%s </div><div class=\"highlight\">有客户投诉，建议核实情况，尽快处理。</div>";


    public static void main(String[] args) {

        System.out.println(
                String.format(complaintTipTpl,new Date(),"诈骗","18158873850")
        );
    }


}
