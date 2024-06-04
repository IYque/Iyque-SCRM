package cn.iyque.controller;


import cn.iyque.domain.IYqueConfig;
import cn.iyque.domain.ResponseResult;
import cn.iyque.service.IYqueConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iYqueConfig")
public class IYqueConfigController {

    @Autowired
    private IYqueConfigService iYqueConfigService;


    /**
     * 获取配置
     * @return
     */
    @GetMapping("/findIYqueConfig")
    public ResponseResult<IYqueConfig> findIYqueConfig(){
        return new ResponseResult<>(
                iYqueConfigService.findIYqueConfig()
        );
    }


    /**
     * 保存或更新配置
     * @param iYqueConfig
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody IYqueConfig iYqueConfig){
        iYqueConfigService.saveOrUpdate(iYqueConfig);

        return new ResponseResult();
    }
}
