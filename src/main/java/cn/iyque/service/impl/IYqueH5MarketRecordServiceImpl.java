package cn.iyque.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.dao.IYqueH5MarketRecordDao;
import cn.iyque.domain.H5MarketRecordCountDto;
import cn.iyque.entity.IYqueH5MarketRecord;
import cn.iyque.service.IYqueH5MarketRecordService;
import cn.iyque.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IYqueH5MarketRecordServiceImpl implements IYqueH5MarketRecordService {

    @Autowired
    private IYqueH5MarketRecordDao iYqueH5MarketRecordDao;


    @Override
    public H5MarketRecordCountDto findH5MarketTab(Long h5MarketId) {
        H5MarketRecordCountDto countDto=new H5MarketRecordCountDto();

        //获取全部
        List<IYqueH5MarketRecord> recordCountDtos = iYqueH5MarketRecordDao
                .findByH5MarketId(h5MarketId);
        if(CollectionUtil.isNotEmpty(recordCountDtos)){
            countDto.setViewTotalPeopleNumber(
                    recordCountDtos.stream()
                            .collect(Collectors.toMap(
                                    IYqueH5MarketRecord::getViewIp,
                                    dto -> dto,
                                    (existing, replacement) -> existing
                            ))
                            .values()
                            .stream()
                            .collect(Collectors.toList()).size()
            );
            countDto.setViewTotalNumber(recordCountDtos.size());
        }

        List<H5MarketRecordCountDto> recordCountDtoList = iYqueH5MarketRecordDao
                .findDailyStatsNative(new Date(), new Date(), h5MarketId, null).getContent();
        if(CollectionUtil.isNotEmpty(recordCountDtoList)){
            countDto.setTdViewTotalNumber(
                    recordCountDtoList.stream().findFirst().get().getTdViewTotalNumber()
            );
            countDto.setTdViewTotalPeopleNumber(
                    recordCountDtoList.stream().findFirst().get().getTdViewTotalPeopleNumber()
            );
        }


        return countDto;
    }

    @Override
    public Page<H5MarketRecordCountDto> findDailyStats(Date startDate, Date endDate, Long h5MarketId, Pageable pageable) {
        return iYqueH5MarketRecordDao.findDailyStatsNative(startDate,endDate,h5MarketId,pageable);
    }

    @Override
    public List<H5MarketRecordCountDto> findH5MarketTrend(Date startDate, Date endDate, Long h5MarketId) {

        List<H5MarketRecordCountDto> recordCountDtos=new ArrayList<>();

        List<H5MarketRecordCountDto> countDtoList = iYqueH5MarketRecordDao
                .findDailyStatsNative(startDate, endDate, h5MarketId, null).getContent();



        //获取连续时间段
         DateUtils.getTimePeriods(startDate, endDate).stream().forEach(k->{

             H5MarketRecordCountDto recordCountDto = H5MarketRecordCountDto.builder()
                     .date(k)
                     .build();

             if(CollectionUtil.isNotEmpty(countDtoList)){
                 Optional<H5MarketRecordCountDto> result = countDtoList.stream()
                         .filter(dto -> k.equals(dto.getDate()))
                         .findFirst();
                 if(result.isPresent()){

                     recordCountDto.setTdViewTotalPeopleNumber(
                             result.get().getTdViewTotalPeopleNumber()
                     );
                     recordCountDto.setTdViewTotalNumber(
                             result.get().getTdViewTotalNumber()
                     );
                     recordCountDto.setViewTotalNumber(
                             result.get().getViewTotalNumber()
                     );
                     recordCountDto.setViewTotalPeopleNumber(
                             result.get().getViewTotalPeopleNumber()
                     );
                 }

             }

             recordCountDtos.add(recordCountDto);
        });
        return recordCountDtos;
    }
}
