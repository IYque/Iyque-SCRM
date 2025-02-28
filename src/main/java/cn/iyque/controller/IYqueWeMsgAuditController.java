package cn.iyque.controller;

import cn.iyque.service.IWeMsgAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
public class IYqueWeMsgAuditController {

    @Autowired
    private IWeMsgAuditService weMsgAuditService;



    @GetMapping("/getXXX")
    public void getXXX() throws Exception {
        weMsgAuditService.synchMsg();
    }
}
