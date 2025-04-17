package cn.iyque.controller;


import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueKf;
import cn.iyque.entity.IYqueKfMsgSub;
import cn.iyque.service.IYqueKfMsgService;
import cn.iyque.service.IYqueKfService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;


/**
 * 客服相关
 */
@RequestMapping("/kf")
@Slf4j
public class IYqueKfController {


    @Autowired
    private IYqueKfService yqueKfService;


    @Autowired
    private IYqueKfMsgService yqueKfMsgService;




    /**
     * 获取客服列表
     * @param iYqueKf
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<IYqueKf> findAll(IYqueKf iYqueKf){

        Page<IYqueKf> iYqueKfs = yqueKfService.findAll(iYqueKf,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(iYqueKfs.getContent(),iYqueKfs.getTotalElements());
    }




    /**
     * 新增或编辑客服
     */
    @PostMapping("/saveOrUpdateKf")
    public ResponseResult saveOrUpdateKf(@RequestBody IYqueKf iYqueKf) {
        yqueKfService.saveOrUpdateKf(iYqueKf);
        return new ResponseResult();
    }




    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueKfService.batchDelete(Arrays.asList(ids));

        return new ResponseResult();
    }



    /**
     * 获取客服会话列表
     * @param iYqueKfMsgSub
     * @return
     */
    @GetMapping("/findKfMsgAll")
    public ResponseResult<IYqueKfMsgSub> findKfMsgAll(IYqueKfMsgSub iYqueKfMsgSub){

        Page<IYqueKfMsgSub> kfMsgSubs = yqueKfMsgService.findAll(iYqueKfMsgSub,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("sendTime").descending()));
        return new ResponseResult(kfMsgSubs.getContent(),kfMsgSubs.getTotalElements());
    }
}
