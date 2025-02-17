package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueUser;
import cn.iyque.entity.IYqueUserCode;
import cn.iyque.service.IYqueUserService;
import cn.iyque.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 企业微信员工相关
 */
@RestController
@RequestMapping("/iYqueUser")
public class IYqueUserController {


    @Autowired
    private IYqueUserService iYqueUserService;


    /**
     * 获取企业微信员工(所有)
     * @return
     */
    @GetMapping("/findIYqueUser")
    public ResponseResult findIYqueUser(){

        List<IYqueUser> iYqueUser = iYqueUserService.findIYqueUser();

        if(CollectionUtil.isNotEmpty(iYqueUser)){
            return new ResponseResult(iYqueUser.stream()
                    .filter(MapUtils.distinctByKey(IYqueUser::getUserId))
                    .collect(Collectors.toList()));
        }
        return new ResponseResult();
    }


    /**
     * 获取企业微信员工(分页)
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/findIYqueUserPage")
    public ResponseResult<IYqueUser> findIYqueUserPage(@RequestParam String name,@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size){
        Page<IYqueUser> iYqueUsers = iYqueUserService.findIYqueUserPage(name,PageRequest.of(page, size, Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueUsers.getContent(),iYqueUsers.getTotalElements());
    }


    /**
     * 同步成员
     * @return
     */
    @PostMapping("/synchIyqueUser")
    public  ResponseResult synchIyqueUser(){

        iYqueUserService.synchIyqueUser();

        return new ResponseResult();
    }



}
