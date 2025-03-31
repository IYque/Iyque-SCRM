package cn.iyque.domain;

import lombok.Data;

@Data
public class IYqueAnalysisHotWordVo {
    //热词
    private String hotWord;

    //热词讨论数量
    private long hotWordDiscussNumber;
}
