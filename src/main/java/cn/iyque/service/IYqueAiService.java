package cn.iyque.service;

import cn.iyque.exception.IYqueException;

public interface IYqueAiService {

    /**
     * 调用ai同步处理通用内容
     * @param content
     * @return
     */
    String aiHandleCommonContent(String content) throws IYqueException;




}
