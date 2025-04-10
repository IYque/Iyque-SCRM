package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueHotWordDao;
import cn.iyque.entity.BaseEntity;
import cn.iyque.entity.IYqueHotWord;
import cn.iyque.entity.IYqueMsgRule;
import cn.iyque.service.IYqueHotWordService;
import cn.iyque.service.IYqueMsgAuditService;
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


    @Autowired
    private IYqueMsgAuditService iYqueMsgAuditService;



    private final String aiHotWordAnalysisTpl = "任务描述：\n" +
            "分析以下聊天内容，判断是否存在意向客户：\n" +
            "%s\n" +
            "聊天内容：\n" +
            "%s\n\n" +
            "分析要求：\n" +
            "1.逐条分析聊天内容，判断是否存在意向客户。\n" +
            "2.如果存在意向客户，需明确意向客户类型，其中`warning`为true表示为意向客户,false为非意向客户 并在 `msg` 字段中描述具体意向行为。\n" +
            "3.最终输出结果必须严格为以下格式结构化输出之一,不可包含其他内容：\n" +
            "- 存在意向客户行为：[{\"warning\":true, \"employeeName\":\"具体员工名称\", \"employeeId\":\"具体员工id\", \"customerName\":\"具体客户名称\", \"customerId\":\"具体客户id\", \"msg\":\"具体意向行为描述\"}]\n" +
            "- 不存在意向客户行为：[{\"warning\":false, \"employeeName\":\"具体员工名称\", \"employeeId\":\"具体员工id\", \"customerName\":\"具体客户名称\", \"customerId\":\"具体客户id\", \"msg\":\"未发现意向行为\"}]";



    @Override
    public Page<IYqueHotWord> findAll(IYqueHotWord iYqueHotWord, Pageable pageable) {

        Specification<IYqueHotWord> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iYqueHotWord.getHotWord())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("hotWord")), "%" + iYqueHotWord.getHotWord().toLowerCase() + "%"));
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
    public List<IYqueHotWord> findAll() {
        return iYqueHotWordDao.findAll();
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

    @Override
    public void aiHotWordAnalysis(List<IYqueHotWord> iYqueHotWords, BaseEntity baseEntity) {
        if(CollectionUtil.isNotEmpty(iYqueHotWords)){
            baseEntity.setMsgAuditType(1);
            String customerMsgData = iYqueMsgAuditService.findCustomerMsgData(baseEntity);

            if(StringUtils.isNotEmpty(customerMsgData)){

//                String prompt = String.format(aiHotWordAnalysisTpl, IYqueMsgRule.formatRules(iYqueMsgRules), customerMsgData);
//
//                log.info("当前聊天内容分析提示词:"+prompt);



            }

        }

    }

}
