package cn.iyque.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iyque.chain.loader.ResourceLoader;
import cn.iyque.chain.loader.ResourceLoaderFactory;
import cn.iyque.dao.IYqueKnowledgeAttachDao;
import cn.iyque.dao.IYqueKnowledgeFragmentDao;
import cn.iyque.dao.IYqueKnowledgeInfoDao;
import cn.iyque.domain.KnowledgeInfoUploadRequest;
import cn.iyque.entity.IYqueKnowledgeAttach;
import cn.iyque.entity.IYqueKnowledgeFragment;
import cn.iyque.entity.IYqueKnowledgeInfo;
import cn.iyque.entity.IYqueMsgAudit;
import cn.iyque.service.IYqueEmbeddingService;
import cn.iyque.service.IYqueKnowledgeInfoService;
import cn.iyque.utils.SnowFlakeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class IYqueKnowledgeInfoServiceImpl implements IYqueKnowledgeInfoService {

    @Autowired
    private IYqueKnowledgeInfoDao knowledgeInfoDao;


    @Autowired
    private IYqueEmbeddingService yqueEmbeddingService;


    @Autowired
    private IYqueKnowledgeAttachDao knowledgeAttachDao;


    @Autowired
    private ResourceLoaderFactory resourceLoaderFactory;


    @Autowired
    private IYqueKnowledgeFragmentDao knowledgeFragmentDao;



    @Override
    public String recover(String content, String openKfId) {
        return null;
    }

    @Override
    public Page<IYqueKnowledgeInfo> findAll(IYqueKnowledgeInfo iYqueKnowledgeInfo, Pageable pageable) {
        Specification<IYqueKnowledgeInfo> spec = Specification.where(null);

        //知识库名称
        if(StringUtils.isNotEmpty(iYqueKnowledgeInfo.getKname())){
            spec = spec.and((root, query, cb) -> cb.like(root.get("kname"), "%" + iYqueKnowledgeInfo.getKname() + "%"));
        }


        return   knowledgeInfoDao.findAll(spec, pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(IYqueKnowledgeInfo iYqueKnowledgeInfo) {

        if(iYqueKnowledgeInfo.getId() != null){
            knowledgeInfoDao.save(iYqueKnowledgeInfo);
            yqueEmbeddingService.createSchema(iYqueKnowledgeInfo.getId());

        }else{
            knowledgeInfoDao.saveAndFlush(iYqueKnowledgeInfo);
        }

    }

    @Override
    public IYqueKnowledgeInfo findKnowledgeInfoById(Long id) {
        return knowledgeInfoDao.findById(id).get();
    }

    @Override
    public void upload(KnowledgeInfoUploadRequest request) {
        storeContent(request.getFile(), request.getKid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeKnowledge(Long id) {

        //删除向量信息
        yqueEmbeddingService.removeByKid(String.valueOf(id));
        //删除知识文档
        knowledgeAttachDao.deleteByKid(id);
        //删除知识片段
        knowledgeFragmentDao.deleteByKid(id);
        //删除知识库
        knowledgeInfoDao.deleteById(id);
    }

    public void storeContent(MultipartFile file, Long kid) {
        String fileName = file.getOriginalFilename();
        List<String> chunkList = new ArrayList<>();
        IYqueKnowledgeAttach knowledgeAttach = new  IYqueKnowledgeAttach();
        knowledgeAttach.setId(SnowFlakeUtils.nextId());
        knowledgeAttach.setKid(kid);
        knowledgeAttach.setDocName(fileName);
        knowledgeAttach.setDocType(fileName.substring(fileName.lastIndexOf(".")+1));
        String content = "";
        ResourceLoader resourceLoader = resourceLoaderFactory.getLoaderByFileType(knowledgeAttach.getDocType());
        List<String> fids = new ArrayList<>();
        try {
            content = resourceLoader.getContent(file.getInputStream());
            chunkList = resourceLoader.getChunkList(content, String.valueOf(kid));
            List<IYqueKnowledgeFragment> knowledgeFragmentList = new ArrayList<>();
            if (CollUtil.isNotEmpty(chunkList)) {
                for (int i = 0; i < chunkList.size(); i++) {
                    String fid = RandomUtil.randomString(16);
                    fids.add(fid);
                    IYqueKnowledgeFragment knowledgeFragment = new IYqueKnowledgeFragment();
                    knowledgeFragment.setKid(kid);
                    knowledgeFragment.setDocId(knowledgeAttach.getId());
                    knowledgeFragment.setIdx(i);
                    knowledgeFragment.setContent(chunkList.get(i));
                    knowledgeFragmentList.add(knowledgeFragment);
                }
            }
            knowledgeFragmentDao.saveAll(knowledgeFragmentList);
        } catch (IOException e) {
           log.error("附件存储异常:"+e.getMessage());
        }
        knowledgeAttach.setContent(content);
        knowledgeAttachDao.save(knowledgeAttach);
        yqueEmbeddingService.storeEmbeddings(chunkList,String.valueOf(kid),String.valueOf(knowledgeAttach.getId()),fids);
    }

}
