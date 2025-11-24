package cn.iyque.service;

import cn.iyque.entity.IYqueTagGroup;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IYqueTagGroupService extends IService<IYqueTagGroup> {

    /**
     * 同步标签
     */
    void synchIYqueTag();



    /**
     * 新增标签组
     *
     * @param tagGroup 标签组
     * @return 结果
     */
    void addTagGroup(IYqueTagGroup tagGroup);


    /**
     * 编辑标签
     * @param tagGroup
     */
    void updateTagGroup(IYqueTagGroup tagGroup);


    /**
     * 获取标签组
     * @return
     */
    List<IYqueTagGroup> findIYqueTagGroups(String groupTagName);



    /**
     * 批量删除标签组
     *
     * @param groupIds 标签组id
     * @return
     */
    void removeGroupTags(String[] groupIds);



}
