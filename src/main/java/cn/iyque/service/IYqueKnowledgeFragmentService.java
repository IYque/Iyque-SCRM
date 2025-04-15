package cn.iyque.service;

import cn.iyque.entity.IYqueKnowledgeAttach;
import cn.iyque.entity.IYqueKnowledgeFragment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYqueKnowledgeFragmentService {

    /**
     * 获取知识库附件片段列表
     * @param iYqueKnowledgeInfo
     * @param pageable
     * @return
     */
    Page<IYqueKnowledgeFragment> findAll(IYqueKnowledgeFragment iYqueKnowledgeInfo, Pageable pageable);
}
