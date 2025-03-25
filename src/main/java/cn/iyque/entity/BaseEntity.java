package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
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

    //预审类型 1:客户；2:客群
    @Transient
    private Integer msgAuditType;
}
