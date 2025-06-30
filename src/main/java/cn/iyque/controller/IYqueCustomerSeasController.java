package cn.iyque.controller;



import cn.iyque.domain.IYqueCustomerSeasVo;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueCustomerSeas;
import cn.iyque.service.IYqueCustomerSeasService;
import cn.iyque.utils.IYqueExcelUtils;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import cn.iyque.utils.ServletUtils;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;



/**
 * 客户公海
 */
@RestController
@RequestMapping("/seas")
public class IYqueCustomerSeasController {


    @Autowired
    IYqueCustomerSeasService yqueCustomerSeasService;


    /**
     * 公海下载模版
     */
    @GetMapping("/export")
    public void export() {

        IYqueExcelUtils.exprotExcel(
                ServletUtils.getResponse(), IYqueCustomerSeas.class, new ArrayList<>(), "客户公海_" + System.currentTimeMillis()
        );

    }


    /**
     * 公海导入
     * @param file
     * @return
     */
    @PostMapping("/importData")
    public ResponseResult importData(IYqueCustomerSeasVo seasVo, MultipartFile file) {
        yqueCustomerSeasService.importData(seasVo,file);
        return new ResponseResult();
    }


    /**
     * 更新公海客户相关动态
     * @param customerSeas
     * @return
     */
    @PostMapping("/updateCustomerSeasState")
    public ResponseResult  updateCustomerSeasState(@RequestBody IYqueCustomerSeas customerSeas){
        yqueCustomerSeasService.updateCustomerSeasState(customerSeas);
        return new ResponseResult();
    }


    /**
     * 获取客户公海列表
     * @param customerSeas
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueCustomerSeas> findAll(IYqueCustomerSeas customerSeas){

        Page<IYqueCustomerSeas> customerInfos = yqueCustomerSeasService.findAll(customerSeas,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(customerInfos.getContent(),customerInfos.getTotalElements());
    }


    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/batchDelete/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueCustomerSeasService.batchDelete(ids);

        return new ResponseResult();
    }


    /**
     * 通过id提醒
     *
     * @param ids id列表
     * @return 结果
     */
    @PostMapping(path = "/distribute/{ids}")
    public ResponseResult distribute(@PathVariable("ids") Long[] ids) throws Exception {

        yqueCustomerSeasService.distribute(ids);

        return new ResponseResult();
    }






}
