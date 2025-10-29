package cn.iyque.dao;

import cn.iyque.domain.H5MarketRecordCountDto;
import cn.iyque.entity.IYqueH5MarketRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IYqueH5MarketRecordDao  extends JpaRepository<IYqueH5MarketRecord,Long>, JpaSpecificationExecutor<IYqueH5MarketRecord> {


    List<IYqueH5MarketRecord> findByH5MarketId(Long h5MarketId);


        @Query(value = "SELECT FORMATDATETIME(r.CREATETIME, 'yyyy-MM-dd') as date, " +
                "COUNT(*) as viewTotalNumber, " +
                "COUNT(DISTINCT r.VIEWIP) as viewTotalPeopleNumber, " +
                "SUM(CASE WHEN CAST(r.CREATETIME AS DATE) = CURRENT_DATE THEN 1 ELSE 0 END) as tdViewTotalNumber, " +
                "COUNT(DISTINCT CASE WHEN CAST(r.CREATETIME AS DATE) = CURRENT_DATE THEN r.VIEWIP ELSE NULL END) as tdViewTotalPeopleNumber " +
                "FROM iyque_h5_market_record r " +
                "WHERE r.DELFLAG = 0 " +
                "AND r.CREATETIME BETWEEN :startDate AND :endDate " +
                "AND r.H5MARKETID = :h5MarketId " +
                "GROUP BY FORMATDATETIME(r.CREATETIME, 'yyyy-MM-dd')",
                countQuery = "SELECT COUNT(DISTINCT FORMATDATETIME(r.CREATETIME, 'yyyy-MM-dd')) " +
                        "FROM iyque_h5_market_record r " +
                        "WHERE r.DELFLAG = 0 " +
                        "AND r.CREATETIME BETWEEN :startDate AND :endDate " +
                        "AND r.H5MARKETID = :h5MarketId",
                nativeQuery = true)
        Page<H5MarketRecordCountDto> findDailyStatsNative(
                @Param("startDate") Date startDate,
                @Param("endDate") Date endDate,
                @Param("h5MarketId") Long h5MarketId,
                Pageable pageable); // 新增 Pageable 参数





}
