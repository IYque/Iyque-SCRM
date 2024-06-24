package cn.iyque.controller;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.entity.IYqueConfig;
import cn.iyque.domain.ResponseResult;
import cn.iyque.service.IYqueConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 系统配置相关
 */
@RestController
@RequestMapping("/iYqueConfig")
public class IYqueConfigController {

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueParamConfig iYqueParamConfig;


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

        if(iYqueParamConfig.getDemo()){

            return new ResponseResult(HttpStatus.ERROR,"当前为演示环境参数无法修改","");
        }
        iYqueConfigService.saveOrUpdate(iYqueConfig);

        return new ResponseResult();
    }


}
