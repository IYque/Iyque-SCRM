package cn.iyque.wxjava.service;

import cn.iyque.service.IYqueConfigService;
import cn.iyque.wxjava.bean.WxOperLogInfo;
import cn.iyque.wxjava.bean.WxOperLogResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 如果调用企业微信接口wxjava未封装，可在该类中拓展
 * (本类拓展：安全管理模块)
 */
@Service
@Slf4j
public class IYqueWxSecurityService {

    @Autowired
    IYqueConfigService iYqueConfigService;


    /**
     * 获取成员操作日志
     * @param info
     * @return
     * @throws Exception
     */
    public WxOperLogResult getMemberOperLog(WxOperLogInfo info) throws Exception {
        String url =  iYqueConfigService.findWxcpservice().getWxCpConfigStorage().getApiUrl("/cgi-bin/security/member_oper_log/list");
        return WxOperLogResult.fromJson(iYqueConfigService.findWxcpservice().post(url, info.toJson()));
    }


    /**
     * 获取管理操作日志
     * @param info
     * @return
     * @throws Exception
     */
    public WxOperLogResult getAdminOperLog(WxOperLogInfo info) throws Exception {
        String url =  iYqueConfigService.findWxcpservice().getWxCpConfigStorage().getApiUrl("/cgi-bin/security/admin_oper_log/list");
        return WxOperLogResult.fromJson(iYqueConfigService.findWxcpservice().post(url, info.toJson()));
    }


}
