package cn.iyque.service.impl;

import cn.iyque.dao.IYqueKnowledgeFragmentDao;
import cn.iyque.entity.IYqueKnowledgeAttach;
import cn.iyque.entity.IYqueKnowledgeFragment;
import cn.iyque.service.IYqueKnowledgeFragmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class IYqueKnowledgeFragmentServiceImpl implements IYqueKnowledgeFragmentService {

    @Autowired
    private IYqueKnowledgeFragmentDao yqueKnowledgeFragmentDao;


    @Override
    public Page<IYqueKnowledgeFragment> findAll(IYqueKnowledgeFragment iYqueKnowledgeInfo, Pageable pageable) {
        Specification<IYqueKnowledgeFragment> spec = Specification.where(null);

        if(iYqueKnowledgeInfo.getDocId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("docId"), iYqueKnowledgeInfo.getKid()));
        }



        return   yqueKnowledgeFragmentDao.findAll(spec, pageable);
    }
}
