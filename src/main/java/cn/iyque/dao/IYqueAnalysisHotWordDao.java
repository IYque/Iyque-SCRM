package cn.iyque.dao;

import cn.iyque.entity.IYqueAnalysisHotWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IYqueAnalysisHotWordDao extends JpaRepository<IYqueAnalysisHotWord,Long>, JpaSpecificationExecutor<IYqueAnalysisHotWord> {
}
