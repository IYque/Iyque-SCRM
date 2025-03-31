package cn.iyque.service;

import cn.iyque.entity.IYqueHotWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IYqueHotWordService {
    /**
     * 获取热词列表
     * @return
     */
    Page<IYqueHotWord> findAll(IYqueHotWord iYqueHotWord, Pageable pageable);


    /**
     * 新增或编辑分类
     * @param iYqueHotWord
     */
    void saveOrUpdate(IYqueHotWord iYqueHotWord);


    /**
     * 删除分类
     * @param ids
     */
    void batchDelete(Long[] ids);
}
