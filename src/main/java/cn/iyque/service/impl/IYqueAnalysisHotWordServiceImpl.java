package cn.iyque.service.impl;

import cn.iyque.dao.IYqueAnalysisHotWordDao;
import cn.iyque.domain.IYqueAnalysisHotWordTabVo;
import cn.iyque.domain.IYqueAnalysisHotWordVo;
import cn.iyque.entity.IYqueAnalysisHotWord;
import cn.iyque.service.IYqueAnalysisHotWordService;
import cn.iyque.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class IYqueAnalysisHotWordServiceImpl implements IYqueAnalysisHotWordService {

    @Autowired
    private IYqueAnalysisHotWordDao yqueAnalysisHotWordDao;

    @Override
    public Page<IYqueAnalysisHotWord> findAll(IYqueAnalysisHotWord iYqueAnalysisHotWord, Pageable pageable) {


        Specification<IYqueAnalysisHotWord> spec = Specification.where(null);

        if(iYqueAnalysisHotWord.getCategoryId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categoryId"), iYqueAnalysisHotWord.getCategoryId()));
        }

        if(iYqueAnalysisHotWord.getHotWordId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("hotWordId"), iYqueAnalysisHotWord.getHotWordId()));
        }

        //按照时间查询
        if (iYqueAnalysisHotWord.getStartTime() != null && iYqueAnalysisHotWord.getEndTime() != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("msgTime"), DateUtils.setTimeToStartOfDay( iYqueAnalysisHotWord.getStartTime()), DateUtils.setTimeToEndOfDay( iYqueAnalysisHotWord.getEndTime())));
        }



        return yqueAnalysisHotWordDao.findAll(spec,pageable);
    }

    @Override
    public List<IYqueAnalysisHotWordVo> hotWordTop5(IYqueAnalysisHotWord iYqueAnalysisHotWord) {
        return null;
    }

    @Override
    public List<IYqueAnalysisHotWordVo> hotWordCategoryTop5(IYqueAnalysisHotWord iYqueAnalysisHotWord) {
        return null;
    }

    @Override
    public IYqueAnalysisHotWordTabVo findHotWordTab() {
        return null;
    }

    @Override
    public void buildAiHotWordAnalysis() {

    }
}
