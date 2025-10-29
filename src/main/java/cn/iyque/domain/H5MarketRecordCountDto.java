package cn.iyque.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class H5MarketRecordCountDto {
    /**
     *  日期
     */
    private String date;

    /**
     * H5访问总数
     */
    private long viewTotalNumber;

    /**
     * h5访问总人数
     */
    private long viewTotalPeopleNumber;

    /**
     * 今日h5访问总数
     */
    private long tdViewTotalNumber;


    /**
     * 今日h5访问总人数
     */
    private long tdViewTotalPeopleNumber;
}
