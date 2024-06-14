package cn.iyque.controller;


import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.IYqueUserCode;
import cn.iyque.domain.ResponseResult;
import cn.iyque.service.IYqueUserCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


/**
 * 客户引流码相关
 */
@RestController
@RequestMapping("/iyQue")
public class IYqueUserCodeController {

    @Autowired
    private IYqueUserCodeService iYqueUserCodeService;


    /**
     * 新增引流码
     * @param iYqueUserCode
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody IYqueUserCode iYqueUserCode) {
        try {
            iYqueUserCodeService.save(iYqueUserCode);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }


    /**
     * 更新引流码
     * @param iYqueUserCode
     * @return
     */
    @PostMapping("/update")
    public ResponseResult update(@RequestBody IYqueUserCode iYqueUserCode){
        try {
            iYqueUserCodeService.update(iYqueUserCode);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }



    /**
     * 获取引流码列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/findIYqueUserCode")
    public ResponseResult<IYqueUserCode> findIYqueUserCode(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){
        Page<IYqueUserCode> iYqueUserCodes = iYqueUserCodeService.findAll(PageRequest.of(page, size, Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueUserCodes.getContent(),iYqueUserCodes.getTotalElements());
    }






    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Integer[] ids) {

        iYqueUserCodeService.batchDelete(ids);

        return new ResponseResult();
    }


//    /**
//     * 活码下发
//     * @param id
//     * @return
//     */
//    @GetMapping("/distributeUserCode/{id}")
//    public RequestBody distributeUserCode(@PathVariable("id") Integer id){
//
//    }
//




}
