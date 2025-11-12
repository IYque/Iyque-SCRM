package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueTag;
import cn.iyque.entity.IYqueTagGroup;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueTagGroupService;
import cn.iyque.service.IYqueTagService;
import cn.iyque.utils.TableSupport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalTagGroupInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 企微标签
 */
@RestController
@RequestMapping("/iYqueTag")
public class IYqueTagController {






    @Autowired
    private IYqueTagGroupService iYqueTagGroupService;


    @Autowired
    private IYqueTagService yqueTagService;


    /**
     * 获取标签
     * @return
     */
    @GetMapping("/findIYqueTag")
    public ResponseResult findIYqueTag(){
        List<WxCpUserExternalTagGroupInfo.Tag> wxCpTags=new ArrayList<>();

        List<IYqueTag> iYqueTagList = yqueTagService.list(new LambdaQueryWrapper<>());
        if(CollectionUtil.isNotEmpty(iYqueTagList)){

            iYqueTagList.stream().forEach(item->{

                WxCpUserExternalTagGroupInfo.Tag tag=new WxCpUserExternalTagGroupInfo.Tag();
                tag.setName(item.getName());
                tag.setId(item.getTagId());
                wxCpTags.add(tag);
            });

        }


        return new ResponseResult(wxCpTags);
    }


    /**
     * 同步标签
     * @return
     */
    @GetMapping("/synchTags")
    public ResponseResult synchTags(){
        try {
            iYqueTagGroupService.synchIYqueTag();
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMsg(),null);
        }
        return new ResponseResult("标签同步中,请稍后刷新查看");
    }


    /**
     * 删除标签组
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public ResponseResult remove(@PathVariable String[] ids)
    {

        try {
            iYqueTagGroupService.removeGroupTags(ids);
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMsg(),null);
        }
        return new ResponseResult();
    }


    /**
     * 新增标签组
     */
    @PostMapping
    public ResponseResult addTagGroup(@RequestBody IYqueTagGroup iYqueTagGroup)
    {


        try {
            iYqueTagGroupService.addTagGroup(iYqueTagGroup);
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMsg(),null);
        }
        return new ResponseResult();


    }

    /**
     * 修改标签组
     */
    @PutMapping
    public ResponseResult updateTagGroup(@RequestBody IYqueTagGroup iYqueTagGroup)
    {


        try {
            iYqueTagGroupService.updateTagGroup(iYqueTagGroup);
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMsg(),null);
        }
        return new ResponseResult();

    }

    /**
     * 标签列表
     * @param yqueTagGroup
     * @return
     */
    @GetMapping("/findIYqueTagGroups")
    public ResponseResult findIYqueTagGroups(IYqueTagGroup yqueTagGroup){

        IPage<IYqueTagGroup> iPage = iYqueTagGroupService.findIYqueTagGroups(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<IYqueTagGroup>(
                TableSupport.buildPageRequest().getPageNum(), TableSupport.buildPageRequest().getPageSize()
        ),yqueTagGroup.getGroupName());

        return new ResponseResult(iPage.getRecords(),iPage.getTotal());
    }



}
