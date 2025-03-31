package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueCategoryDao;
import cn.iyque.entity.IYqueCategory;
import cn.iyque.service.IYqueCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class IYqueCategoryServiceImpl implements IYqueCategoryService {

    @Autowired
    private IYqueCategoryDao iYqueCategoryDao;

    @Override
    public List<IYqueCategory> findAll() {

        return iYqueCategoryDao.findAll(Sort.by("createTime").descending());
    }

    @Override
    public void saveOrUpdate(IYqueCategory iYqueCategory) {
        if(iYqueCategory.getId() != null){
            iYqueCategory.setUpdateTime(new Date());
        }else{
            iYqueCategory.setCreateTime(new Date());
            iYqueCategory.setUpdateTime(new Date());
        }
        iYqueCategoryDao.saveAndFlush(iYqueCategory);
    }

    @Override
    public void batchDelete(Long[] ids) {

        List<IYqueCategory> iYqueCategorys = iYqueCategoryDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueCategorys)){
            iYqueCategorys.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueCategoryDao.saveAndFlush(k);

                }catch (Exception e){
                    log.error("分类删除失败:"+e.getMessage());
                }

            });

        }

    }
}
