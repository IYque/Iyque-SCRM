package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.CodeStateConstant;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueShortLinkDao;
import cn.iyque.domain.IYqueKvalStrVo;
import cn.iyque.entity.*;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.*;
import cn.iyque.utils.SnowFlakeUtils;
import cn.iyque.dao.IYqueSynchDataRecordDao;
import cn.iyque.enums.SynchDataRecordType;
import org.springframework.scheduling.annotation.Async;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionCreateResult;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionInfo;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionRequest;
import me.chanjar.weixin.cp.bean.external.acquisition.WxCpCustomerAcquisitionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IYqueShortLinkServiceImpl implements IYqueShortLinkService {

    @Autowired
    private IYqueConfigService iYqueConfigService;

    @Autowired
    private IYqueShortLinkDao iYqueShortLinkDao;

    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;


    @Autowired
    private IYqueAnnexPeriodService iYqueAnnexPeriodService;


    @Autowired
    private IYquePeriodMsgAnnexService iYquePeriodMsgAnnexService;

    @Autowired
    private IYqueSynchDataRecordDao iYqueSynchDataRecordDao;

    @Autowired
    private IYqueUserService iYqueUserService;

    @Override
    public Page<IYqueShortLink> findAll(Pageable pageable) {
        return iYqueShortLinkDao.findAll(pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(IYqueShortLink shortLink) throws Exception {

        try {
            shortLink.setCreateTime(new Date());
            shortLink.setUpdateTime(new Date());
            shortLink.setCodeState(CodeStateConstant.LINK_CODE_STATE+ SnowFlakeUtils.nextId());
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
            WxCpCustomerAcquisitionRequest request=new WxCpCustomerAcquisitionRequest();
            request.setLinkName(shortLink.getCodeName());
            request.setSkipVerify(shortLink.getSkipVerify());
            WxCpCustomerAcquisitionInfo.Range range=new WxCpCustomerAcquisitionInfo.Range();
            range.setUserList(ListUtil.toList(shortLink.getUserId().split(",")));
            request.setRange(range);

            WxCpCustomerAcquisitionCreateResult createResult = wxcpservice.getExternalContactService().customerAcquisitionLinkCreate(
                    request
            );

                if(createResult == null ){
                    throw new IYqueException("获客短链创建失败");
                }



                WxCpCustomerAcquisitionInfo.Link link = createResult.getLink();

                if(null == link){
                    throw new IYqueException("获客短链创建失败");
                }

                if(null != link && StrUtil.isNotEmpty(link.getLinkId())
                        && StrUtil.isNotEmpty(link.getUrl())){
                    shortLink.setConfigId(link.getLinkId());
                    shortLink.setCodeUrl(link.getUrl()+"?customer_channel="+shortLink.getCodeState() );

                    iYqueShortLinkDao.save(shortLink);


                    //时段欢迎语附件
                    if(shortLink.isStartPeriodAnnex()){


                        List<IYqueAnnexPeriod> periodAnnexLists=shortLink.getPeriodAnnexLists();
                        if(CollectionUtil.isNotEmpty(periodAnnexLists)){
                            //时段附件
                            List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                            periodAnnexLists.stream().forEach(k->{
                                k.setMsgId(shortLink.getId());
                            });

                            //存储时段
                            iYqueAnnexPeriodService.saveAll(periodAnnexLists);
                            //存储时段附件
                            periodAnnexLists.stream().forEach(k->{
                                List<IYquePeriodMsgAnnex> periodMsgAnnexList = k.getPeriodMsgAnnexList();
                                if(CollectionUtil.isNotEmpty(periodMsgAnnexList)){
                                    periodMsgAnnexList.stream().forEach(periodMsgAnnex->{
                                        periodMsgAnnex.setAnnexPeroidId(k.getId());
                                    });
                                    iYquePeriodMsgAnnexes.addAll(periodMsgAnnexList);
                                }
                            });

                            iYquePeriodMsgAnnexService.saveAll(iYquePeriodMsgAnnexes);

                        }
                    }else{

                        List<IYqueMsgAnnex> annexLists = shortLink.getAnnexLists();
                        if(CollectionUtil.isNotEmpty(annexLists)){
                            annexLists.stream().forEach(k->{
                                k.setMsgId(shortLink.getId());
                            });
                            iYqueMsgAnnexService.saveAll(annexLists);
                        }
                    }
                }
        }catch (Exception e){
            log.error("获客链接，创建失败:"+e.getMessage());
           throw e;
        }



    }

    @Override
    public List<IYqueKvalStrVo> findIYqueShorkLinkKvs() {
        List<IYqueKvalStrVo> iYqueKvalVos=new ArrayList<>();


        List<IYqueShortLink> iYqueShortLinks = iYqueShortLinkDao.findAll();
        if(CollectionUtil.isNotEmpty(iYqueShortLinks)){
            iYqueShortLinks.stream().forEach(k->{
                iYqueKvalVos.add(
                        IYqueKvalStrVo.builder()
                                .val(k.getId().toString())
                                .key(k.getCodeName())
                                .build()
                );

            });
        }



        return iYqueKvalVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(IYqueShortLink shortLink) throws Exception {

        try {
            IYqueShortLink oldShortLink = this.findIYqueShortLinkById(shortLink.getId());
            if(null != oldShortLink){

                shortLink.setCodeState(oldShortLink.getCodeState());
                shortLink.setConfigId(oldShortLink.getConfigId());
                WxCpService wxcpservice = iYqueConfigService.findWxcpservice();
                WxCpCustomerAcquisitionRequest request=new WxCpCustomerAcquisitionRequest();
                request.setLinkId(oldShortLink.getConfigId());
                request.setLinkName(shortLink.getCodeName());
                request.setSkipVerify(shortLink.getSkipVerify());
                WxCpCustomerAcquisitionInfo.Range range=new WxCpCustomerAcquisitionInfo.Range();
                range.setUserList(ListUtil.toList(shortLink.getUserId().split(",")));
                request.setRange(range);

                WxCpBaseResp wxCpBaseResp
                        = wxcpservice.getExternalContactService().customerAcquisitionUpdate(request);

                if(null == wxCpBaseResp){
                    throw new IYqueException("获客短链创建失败");
                }

                 if(IYqueContant.COMMON_STATE.equals(wxCpBaseResp.getErrcode().intValue())){

                     shortLink.setUpdateTime(new Date());
                     iYqueShortLinkDao.saveAndFlush(shortLink);


                     if(shortLink.isStartPeriodAnnex()){//开启时段欢迎语

                         List<IYqueAnnexPeriod> periodAnnexLists=shortLink.getPeriodAnnexLists();
                         if(CollectionUtil.isNotEmpty(periodAnnexLists)){

                             //时段附件
                             List<IYquePeriodMsgAnnex> iYquePeriodMsgAnnexes=new ArrayList<>();
                             periodAnnexLists.stream().forEach(k->{
                                 k.setMsgId(shortLink.getId());
                             });

                             //存储时段
                             List<IYqueAnnexPeriod> oldIYqueAnnexPeriod = iYqueAnnexPeriodService
                                     .findIYqueAnnexPeriodByMsgId(shortLink.getId());

                             if(CollectionUtil.isNotEmpty(oldIYqueAnnexPeriod)){
                                 iYqueAnnexPeriodService.deleteIYqueAnnexPeriodByMsgId(shortLink.getId());
                                 iYquePeriodMsgAnnexService.deleteAllByAnnexPeroidIdIn(
                                         oldIYqueAnnexPeriod.stream().map(IYqueAnnexPeriod::getId).collect(Collectors.toList())
                                 );
                             }
                             iYqueAnnexPeriodService.saveAll(periodAnnexLists);


                             periodAnnexLists.stream().forEach(k->{
                                 List<IYquePeriodMsgAnnex> periodMsgAnnexList = k.getPeriodMsgAnnexList();
                                 if(CollectionUtil.isNotEmpty(periodMsgAnnexList)){
                                     periodMsgAnnexList.stream().forEach(periodMsgAnnex->{
                                         periodMsgAnnex.setAnnexPeroidId(k.getId());
                                     });
                                     iYquePeriodMsgAnnexes.addAll(periodMsgAnnexList);
                                 }
                             });
                             iYquePeriodMsgAnnexService.saveAll(iYquePeriodMsgAnnexes);


                         }

                     }else{
                         iYqueMsgAnnexService.deleteIYqueMsgAnnexByMsgId(shortLink.getId());
                         List<IYqueMsgAnnex> annexLists = shortLink.getAnnexLists();
                         if(CollectionUtil.isNotEmpty(annexLists)){
                             annexLists.stream().forEach(k->{
                                 k.setMsgId(shortLink.getId());
                             });
                             iYqueMsgAnnexService.saveAll(annexLists);
                         }

                     }





                 }else{
                     log.error("获客短链更新失败:"+wxCpBaseResp.getErrmsg());
                 }

            }

        }catch (Exception e){
            log.error("获客短链更新失败:"+e.getMessage());
            throw e;
        }

    }

    @Override
    public IYqueShortLink findIYqueShortLinkById(Long id) {
        return iYqueShortLinkDao.getById(id);
    }

    @Override
    public void batchDelete(Long[] ids) {

        List<IYqueShortLink> iYqueShortLinks = iYqueShortLinkDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueShortLinks)){
            iYqueShortLinks.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueConfigService.findWxcpservice().getExternalContactService().customerAcquisitionLinkDelete(k.getConfigId());
                    iYqueShortLinkDao.saveAndFlush(k);
                }catch (Exception e){
                    log.error("获客链接删除失败:"+e.getMessage());
                }

            });

        }

    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void synchShortLink() {
        try {
            log.info("开始同步获客外链...");

            List<IYqueShortLink> shortLinks = new ArrayList<>();
            WxCpService wxcpservice = iYqueConfigService.findWxcpservice();

            String cursor = null;
            int limit = 100;

            // 获取上次同步的游标位置
            IYqueSynchDataRecord lastRecord = iYqueSynchDataRecordDao
                .findTopBySynchDataTypeOrderByCreateTimeDesc(SynchDataRecordType.RECORD_TYPE_SYNCH_SHORT_LINK.getCode());

            if (lastRecord != null && StrUtil.isNotEmpty(lastRecord.getNextCursor())) {
                cursor = lastRecord.getNextCursor();
            }

            do {
                // 调用企业微信API获取获客链接列表
                WxCpCustomerAcquisitionList acquisitionList = wxcpservice.getExternalContactService()
                    .customerAcquisitionLinkList(limit, cursor);

                if (acquisitionList != null && CollectionUtil.isNotEmpty(acquisitionList.getLinkIdList())) {
                    for (String linkId : acquisitionList.getLinkIdList()) {
                        try {
                            // 获取链接详情
                            WxCpCustomerAcquisitionInfo linkDetail = wxcpservice.getExternalContactService()
                                .customerAcquisitionLinkGet(linkId);

                            if (linkDetail != null) {
                                IYqueShortLink shortLink = convertToShortLink(linkDetail);
                                if (shortLink != null) {
                                    shortLinks.add(shortLink);
                                }
                            }
                        } catch (Exception e) {
                            log.error("获取获客链接详情失败，linkId: {}, 错误: {}", linkId, e.getMessage());
                        }
                    }
                }

                cursor = acquisitionList != null ? acquisitionList.getNextCursor() : null;

                // 记录同步进度
                if (StrUtil.isNotEmpty(cursor)) {
                    iYqueSynchDataRecordDao.save(
                        IYqueSynchDataRecord.builder()
                            .synchDataType(SynchDataRecordType.RECORD_TYPE_SYNCH_SHORT_LINK.getCode())
                            .nextCursor(cursor)
                            .createTime(new Date())
                            .build()
                    );
                }

            } while (StrUtil.isNotEmpty(cursor));

            // 批量保存同步的数据
            if (CollectionUtil.isNotEmpty(shortLinks)) {
                // 分批保存，避免一次性保存过多数据
                ListUtil.partition(shortLinks, 50).forEach(batch -> {
                    try {
                        iYqueShortLinkDao.saveAll(batch);
                        log.info("保存获客外链数据，数量: {}", batch.size());
                    } catch (Exception e) {
                        log.error("保存获客外链数据失败: {}", e.getMessage());
                    }
                });
            }

            log.info("获客外链同步完成，共同步 {} 条数据", shortLinks.size());

        } catch (Exception e) {
            log.error("获客外链同步失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 将企业微信获客链接信息转换为本地实体对象
     */
    private IYqueShortLink convertToShortLink(WxCpCustomerAcquisitionInfo linkDetail) {
        if (linkDetail == null || linkDetail.getLink() == null) {
            return null;
        }

        try {
            WxCpCustomerAcquisitionInfo.Link link = linkDetail.getLink();
            WxCpCustomerAcquisitionInfo.Range range = linkDetail.getRange();

            IYqueShortLink shortLink = new IYqueShortLink();

            // 基本信息
            shortLink.setCodeName(link.getLinkName());
            shortLink.setConfigId(link.getLinkId());
            shortLink.setCodeUrl(link.getUrl());
            // 从linkDetail中获取skipVerify信息
            shortLink.setSkipVerify(linkDetail.getSkipVerify() != null ? linkDetail.getSkipVerify() : false);
            shortLink.setCreateTime(new Date());
            shortLink.setUpdateTime(new Date());
            shortLink.setDelFlag(0);

            // 生成渠道标识
            shortLink.setCodeState(CodeStateConstant.LINK_CODE_STATE + SnowFlakeUtils.nextId());

            // 处理员工信息
            if (range != null && CollectionUtil.isNotEmpty(range.getUserList())) {
                shortLink.setUserId(String.join(",", range.getUserList()));

                // 补全员工姓名
                List<String> userNames = new ArrayList<>();
                for (String userId : range.getUserList()) {
                    try {
                        IYqueUser user = iYqueUserService.findOrSaveUser(userId);
                        if (user != null && StrUtil.isNotEmpty(user.getName())) {
                            userNames.add(user.getName());
                        } else {
                            userNames.add(userId); // 如果获取不到姓名，使用userId
                        }
                    } catch (Exception e) {
                        log.warn("获取员工信息失败，userId: {}, 错误: {}", userId, e.getMessage());
                        userNames.add(userId);
                    }
                }
                shortLink.setUserName(String.join(",", userNames));
            }

            // 设置默认值
            shortLink.setIsExclusive(false);
            shortLink.setStartPeriodAnnex(false);
            shortLink.setRemarkType(1); // 默认备注类型

            return shortLink;

        } catch (Exception e) {
            log.error("转换获客链接数据失败: {}", e.getMessage(), e);
            return null;
        }
    }
}
