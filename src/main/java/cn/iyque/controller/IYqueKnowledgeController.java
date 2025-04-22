package cn.iyque.controller;


import cn.iyque.chain.vectorstore.IYqueVectorStore;
import cn.iyque.domain.KnowledgeInfoUploadRequest;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueKnowledgeAttach;
import cn.iyque.entity.IYqueKnowledgeFragment;
import cn.iyque.entity.IYqueKnowledgeInfo;
import cn.iyque.service.*;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;


/**
 * 知识库
 */
@RequestMapping("/knowledge")
@Slf4j
@RestController
public class IYqueKnowledgeController {

    @Autowired
    private IYqueKnowledgeInfoService iYqueKnowledgeInfoService;

    @Autowired
    private IYqueKnowledgeAttachService yqueKnowledgeAttachService;


    @Autowired
    private IYqueKnowledgeFragmentService yqueKnowledgeFragmentService;

//    @Autowired
//    private IYqueEmbeddingService yqueEmbeddingService;
//
//    @Autowired
//    private IYqueVectorStore iYqueVectorStore;

//    @Autowired
//    private IYqueAiService iYqueAiService;
//
//    @GetMapping("/getXXX")
//    public ResponseResult getXXX(String content,String kid){
//
//        List<Float> queryVector = yqueEmbeddingService.getQueryVector(content, kid);
//
//        //向量库检索相关数据
//        List<String> nearest = iYqueVectorStore
//                .nearest(queryVector, kid);
//
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("问题：").append(content).append("\n\n");
//        sb.append("参考内容：\n");
//
//
//        nearest.stream().forEach(k->{
//            sb.append("- ").append(k);
//        });
//
//        sb.append("请根据以上问题和参考内容，生成简洁准确的回答。\n");
//        sb.append("要求：\n");
//        sb.append("1. 须严格根据我给你的系统上下文内容原文进行回答；\n");
//        sb.append("2. 请不要自己发挥,回答时保持原来文本的段落层级；\n");
//        sb.append("3. 如果没有用户需要的相关内容，则返回无相关内容。");
//
//
//        System.out.println(sb.toString());
////        String s = iYqueAiService.aiHandleCommonContent(sb.toString());
//
//
//        return new ResponseResult();
//    }


    /**
     * 获取知识库列表
     * @param knowledgeInfo
     * @return
     */
    @GetMapping("/findMsgAuditByPage")
    public ResponseResult<IYqueKnowledgeInfo> findMsgAuditByPage(IYqueKnowledgeInfo knowledgeInfo){

        Page<IYqueKnowledgeInfo> iYqueKnowledgeInfos = iYqueKnowledgeInfoService.findAll(knowledgeInfo,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(iYqueKnowledgeInfos.getContent(),iYqueKnowledgeInfos.getTotalElements());
    }



    /**
     * 新增知识库
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody IYqueKnowledgeInfo knowledgeInfo) {
        knowledgeInfo.setCreateTime(new Date());
        iYqueKnowledgeInfoService.saveOrUpdate(knowledgeInfo);
        return new ResponseResult();
    }

    /**
     * 上传知识库附件
     */
    @PostMapping(value = "/attach/upload")
    public ResponseResult upload(KnowledgeInfoUploadRequest request){
        iYqueKnowledgeInfoService.upload(request);
        return new ResponseResult();
    }


    /**
     * 删除知识库
     */
    @DeleteMapping("/remove/{id}")
    public ResponseResult remove(@PathVariable Long id){
        iYqueKnowledgeInfoService.removeKnowledge(id);
        return new ResponseResult();
    }


    /**
     * 查询知识附件信息列表
     */
    @GetMapping("/detail/{kid}")
    public  ResponseResult<IYqueKnowledgeAttach> attach(@PathVariable Long kid){
        Page<IYqueKnowledgeAttach> iYqueKnowledgeInfos = yqueKnowledgeAttachService.findAll(IYqueKnowledgeAttach.builder()
                        .kid(kid)
                        .build(),
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(iYqueKnowledgeInfos.getContent(),iYqueKnowledgeInfos.getTotalElements());
    }




    /**
     * 删除知识库附件
     *
     */
    @DeleteMapping("/attach/remove/{docId}")
    public ResponseResult removeAttach(@PathVariable Long docId) {
        yqueKnowledgeAttachService.removeKnowledgeAttach(docId);
        return new ResponseResult();
    }


    /**
     * 查询知识片段
     */
    @GetMapping("/fragment/list/{docId}")
    public ResponseResult<IYqueKnowledgeFragment> fragmentList(@PathVariable Long docId) {
        Page<IYqueKnowledgeFragment> iYqueKnowledgeFragments = yqueKnowledgeFragmentService.findAll(IYqueKnowledgeFragment.builder()
                        .docId(docId)
                        .build(),
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(iYqueKnowledgeFragments.getContent(),iYqueKnowledgeFragments.getTotalElements());
    }




}
