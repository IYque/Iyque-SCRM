package cn.iyque.controller;


import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueH5Market;
import cn.iyque.service.IYqueH5MarketService;
import cn.iyque.utils.TableSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * H5营销
 */
@RestController
@RequestMapping("/h5Market")
public class IYqueH5MarketController {

    @Autowired
    private IYqueH5MarketService yqueH5MarketService;

    /**
     * 新增或更新
     * @param iYqueH5Market
     * @return
     */
    @PostMapping("/addOrUpdate")
    public ResponseResult addOrUpdate(@RequestBody IYqueH5Market iYqueH5Market) {
        try {
            yqueH5MarketService.addOrUpdate(iYqueH5Market);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }

    /**
     * 获取列表
     * @return
     */
    @GetMapping("/findH5Markets")
    public ResponseResult<IYqueH5Market> findH5Markets(
            IYqueH5Market iYqueH5Market){
        Page<IYqueH5Market> iYqueH5Markets = yqueH5MarketService.findAll(
                iYqueH5Market,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(),
                        Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueH5Markets.getContent(),iYqueH5Markets.getTotalElements());
    }


    /**
     * 移动端获取H5营销详情
     * @param marketId
     * @return
     */
    @GetMapping("/findWeH5MarketById/{marketId}")
    public ResponseResult<IYqueH5Market>  findWeH5MarketById(@PathVariable Long marketId){
        IYqueH5Market weH5Market
                = yqueH5MarketService.findWeH5MarketById(marketId);

        return new ResponseResult(weH5Market);

    }

    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        yqueH5MarketService.batchDelete(ids);

        return new ResponseResult();
    }


}
