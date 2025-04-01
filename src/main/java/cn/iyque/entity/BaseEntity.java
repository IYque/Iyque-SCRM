package cn.iyque.entity;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Transient;
import java.util.Date;

@Data
public class BaseEntity {

    @Transient
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @Transient
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    //规则类型1:客户规则；2:客群规则 3:意向客户分析 4:意向群友分析
    @Transient
    private Integer msgAuditType;
}
