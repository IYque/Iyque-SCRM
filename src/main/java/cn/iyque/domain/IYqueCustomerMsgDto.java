package cn.iyque.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 客户会话内容导出
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ColumnWidth(25)
public class IYqueCustomerMsgDto {

    //发送人
    @ExcelProperty(value = "发送人")
    private String fromName;

    //接收人
    @ExcelProperty(value = "接收人")
    private String acceptName;

    //发送内容
    @ExcelProperty(value = "发送内容")
    private String content;

    //发送时间
    @ExcelProperty(value = "发送时间")
    private Date sendTime;
}
