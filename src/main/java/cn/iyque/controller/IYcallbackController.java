package cn.iyque.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.iyque.domain.IYQueCallback;
import cn.iyque.domain.IYqueCallBackBaseMsg;
import cn.iyque.entity.IYqueConfig;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueDefaultMsgService;
import cn.iyque.utils.IYqueCryptUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.constant.WxCpConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * 系统回调
 */
@RestController
@RequestMapping("/iycallback")
@Slf4j
public class IYcallbackController {


    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueDefaultMsgService iYqueDefaultMsgService;


    /**
     * get数据校验
     * @param queCallback
     * @return
     */
    @GetMapping(value = "/handle")
    public String handle(IYQueCallback queCallback) {

        String tip="error";
        IYqueConfig iYqueConfig = iYqueConfigService.findIYqueConfig();

        if(StrUtil.isNotEmpty(iYqueConfig.getToken())
        &&StrUtil.isNotEmpty(iYqueConfig.getEncodingAESKey())
        &&StrUtil.isNotEmpty(iYqueConfig.getCorpId())){
            IYqueCryptUtil iYqueCryptUtil = new IYqueCryptUtil(iYqueConfig.getToken(), iYqueConfig.getEncodingAESKey(), iYqueConfig.getCorpId());

            try {
                if(StrUtil.isEmpty(queCallback.getEchostr())){
                    return "success";
                }
                tip=iYqueCryptUtil.verifyURL(queCallback.getMsg_signature(), queCallback.getTimestamp(), queCallback.getNonce(), queCallback.getEchostr());
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }

        return tip;
    }


    /**
     *  post数据校验
     * @param msg
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    @PostMapping(value = "/handle")
    public String handle(@RequestBody String msg, @RequestParam(name = "msg_signature") String signature,
                         String timestamp, String nonce) {

        IYqueConfig iYqueConfig = iYqueConfigService.findIYqueConfig();
        IYqueCryptUtil iYqueCryptUtil = new IYqueCryptUtil(iYqueConfig.getToken(), iYqueConfig.getEncodingAESKey(), iYqueConfig.getCorpId());
        try {
            String decrypt = iYqueCryptUtil.decrypt(signature, timestamp, nonce, msg);

            IYqueCallBackBaseMsg callBackBaseMsg = XmlUtil.xmlToBean(XmlUtil.parseXml(decrypt).getFirstChild(), IYqueCallBackBaseMsg.class);
            log.info("活码欢迎语回调"+callBackBaseMsg);
            //当前为添加客户事件处理逻辑
            if(null != callBackBaseMsg
            &&WxCpConsts.ExternalContactChangeType.ADD_EXTERNAL_CONTACT.equals(callBackBaseMsg.getChangeType())
            &&WxCpConsts.EventType.CHANGE_EXTERNAL_CONTACT.equals(callBackBaseMsg.getEvent())){
                iYqueDefaultMsgService.sendWelcomeMsg(callBackBaseMsg);
            }


            return decrypt;
        } catch (Exception e) {
            String sRespData = iYqueCryptUtil.getTextRespData("success");
            return iYqueCryptUtil.encrypt(sRespData);
        }
    }






}
