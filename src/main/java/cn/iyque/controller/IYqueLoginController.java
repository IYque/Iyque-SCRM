package cn.iyque.controller;


import cn.iyque.annotation.RateLimit;
import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.IYQueAuthInfo;
import cn.iyque.domain.JwtResponse;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueConfig;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * 系统登录地址
 */
@RestController
@RequestMapping("/iYqueSys")
@Slf4j
public class IYqueLoginController {



    @Autowired
    private IYqueParamConfig iYqueParamConfig;

    @Autowired
    private IYqueConfigService iYqueConfigService;


    /**
     * 简单的登录
     * @return
     */
    @PostMapping("/login")
    @RateLimit(attempts = 5, lockTime = 300) // 5次失败后锁定5分钟
    public ResponseResult<JwtResponse> login(@RequestBody IYQueAuthInfo iQyqueAuthInfo){


        if(iYqueParamConfig.getUserName().equals(iQyqueAuthInfo.getUsername())
          &&iYqueParamConfig.getPwd().equals(iQyqueAuthInfo.getPassword())
        ){

            return new ResponseResult<>(JwtResponse.builder()
                    .token(JwtUtils.generateToken(iYqueParamConfig.getUserName()))
                    .build());

        }
        return new ResponseResult<>(HttpStatus.ERROR,"账号或密码错误",null);
    }


    /**
     * 企微h5登录
     * @param authCode
     * @return
     */
    @GetMapping("/weComLogin")
    public  ResponseResult<JwtResponse> weComLogin(String authCode) {

        try {
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            WxCpOauth2UserInfo userInfo = wxcpservice.getOauth2Service().getUserInfo(authCode);
            if(null != userInfo && StringUtils.isNotEmpty(userInfo.getUserId())){
                return new ResponseResult<>(JwtResponse.builder()
                        .token(JwtUtils.generateToken(userInfo.getUserId()))
                        .build());
            }
        }catch (Exception e){
            log.error("企微h5登录失败:"+e.getMessage());
            return new ResponseResult<>(HttpStatus.ERROR,"企微h5登录失败",null);
        }


        return new ResponseResult<>(HttpStatus.ERROR,"账号登录失败",null);

    }



    /**
     * 企微h5回掉地址
     * @param redirectUrl
     * @return
     */
    @GetMapping("/weComRedirect")
    public ResponseResult weComRedirect(String redirectUrl){

        StringBuilder sb=new StringBuilder();

        try {
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            sb.append(
                    wxcpservice.getOauth2Service()
                            .buildAuthorizationUrl(redirectUrl,"iYque","snsapi_privateinfo")
            );

        }catch (Exception e){
            log.error("企微h5登录失败:"+e.getMessage());
            return new ResponseResult<>(HttpStatus.ERROR,"企微h5登录失败",null);
        }

        return new ResponseResult(
                sb.toString()
        );
    }










}
