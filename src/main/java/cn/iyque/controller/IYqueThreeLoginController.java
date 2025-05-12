package cn.iyque.controller;

import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.JwtResponse;
import cn.iyque.domain.ResponseResult;
import cn.iyque.domain.ThreeLoginInfo;
import cn.iyque.service.IYqueGiteeOAuthService;
import cn.iyque.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;


/**
 * 三方登录相关,目前兼容gitee
 */
@RestController
@RequestMapping("/iYqueSys/threeLogin")
public class IYqueThreeLoginController {

    @Autowired
    IYqueParamConfig yqueParamConfig;


    @Autowired
    IYqueGiteeOAuthService giteeOAuthService;


    /**
     * 获取登录相关信息
     * @return
     */
    @GetMapping("/findThreeLoginInfo")
    public ResponseResult findThreeLoginInfo(){
        IYqueParamConfig.ThreeLoginParam threeLoginParam = yqueParamConfig.getThreeLoginParam();

        ThreeLoginInfo threeLoginInfo=new ThreeLoginInfo();
        threeLoginInfo.setStartThreeLogin(
                threeLoginParam.isStartThreeLogin()
        );

        IYqueParamConfig.GiteeLoginParam giteeLoginParam = threeLoginParam.getGiteeLoginParam();

        threeLoginInfo.setThreeLoginUrl(
                MessageFormat.format(
                        giteeLoginParam.getThreeLoginUrl(),giteeLoginParam.getClientId(),giteeLoginParam.getRedirectUri()
                )
        );
        return new ResponseResult(threeLoginInfo);
    }


    /**
     * gitee授权登录逻辑
     * @param code
     * @return
     */
    @GetMapping("/giteeLogin/{code}")
      public ResponseResult<JwtResponse> giteeLogin(@PathVariable String code){

        String iYqueLoginToken = giteeOAuthService.getIYqueLoginToken(code);

        if(StringUtils.isNotEmpty(iYqueLoginToken)){
            return new ResponseResult<>(JwtResponse.builder()
                    .token(iYqueLoginToken)
                    .build());
        }
        return new ResponseResult<>(HttpStatus.ERROR,"登录异常,请重新授权登录",null);
      }


}
