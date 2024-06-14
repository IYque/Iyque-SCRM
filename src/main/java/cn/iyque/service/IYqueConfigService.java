package cn.iyque.service;

import cn.iyque.domain.IYqueConfig;
import cn.iyque.exception.IYqueException;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;

public interface IYqueConfigService {

    IYqueConfig findIYqueConfig();

    void saveOrUpdate(IYqueConfig iYqueConfig);

    WxCpService findWxcpservice() throws IYqueException;
}
