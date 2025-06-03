package cn.iyque.controller;


import cn.iyque.domain.IYQueCustomerInfo;
import cn.iyque.domain.ResponseResult;
import cn.iyque.service.IYqueCustomerInfoService;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 客户相关
 */
@RestController
@RequestMapping("/customerInfo")
public class IYqueCustomerInfoController {


    @Autowired
    private IYqueCustomerInfoService customerInfoService;


    /**
     * 获取客户列表
     * @param iyQueCustomerInfo
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYQueCustomerInfo> findAll(IYQueCustomerInfo iyQueCustomerInfo){

        Page<IYQueCustomerInfo> customerInfos = customerInfoService.findAll(iyQueCustomerInfo,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("addTime").descending()));
        return new ResponseResult(customerInfos.getContent(),customerInfos.getTotalElements());
    }


    /**
     * 同步客户
     * @return
     */
    @PostMapping("/synchCustomer")
    public ResponseResult synchCustomer(){

        customerInfoService.synchCustomer();

        return new ResponseResult("客户真正同步中,请稍后查看");

    }



}
