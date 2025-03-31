package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueHotWordDao;
import cn.iyque.entity.IYqueHotWord;
import cn.iyque.service.IYqueHotWordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class IYqueHotWordServiceImpl implements IYqueHotWordService {

    @Autowired
    private IYqueHotWordDao iYqueHotWordDao;

    @Override
    public Page<IYqueHotWord> findAll(IYqueHotWord iYqueHotWord, Pageable pageable) {

        Specification<IYqueHotWord> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iYqueHotWord.getHotWord())){
            spec = spec.and((root, query, cb) -> cb.like(root.get("hotWord"), iYqueHotWord.getHotWord()));
        }


        if(iYqueHotWord.getCategoryId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categoryId"), iYqueHotWord.getCategoryId()));
        }


        return iYqueHotWordDao.findAll(spec,pageable);
    }

    @Override
    public void saveOrUpdate(IYqueHotWord iYqueHotWord) {


        if(iYqueHotWord.getId() != null){
            iYqueHotWord.setUpdateTime(new Date());
        }else{
            iYqueHotWord.setCreateTime(new Date());
            iYqueHotWord.setUpdateTime(new Date());
        }
        iYqueHotWordDao.saveAndFlush(iYqueHotWord);

    }

    @Override
    public void batchDelete(Long[] ids) {

        List<IYqueHotWord> iYqueHotWords = iYqueHotWordDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueHotWords)){
            iYqueHotWords.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueHotWordDao.saveAndFlush(k);

                }catch (Exception e){
                    log.error("热词删除失败:"+e.getMessage());
                }

            });

        }


    }
}
