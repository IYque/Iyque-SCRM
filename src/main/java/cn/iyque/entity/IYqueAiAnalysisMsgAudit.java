package cn.iyque.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "iyque_ai_analysis_msg_audit")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueAiAnalysisMsgAudit {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //是否预警，true有异常行为,false无异常行为
    private Boolean warning;
    //员工名称
    private String employeeName;
    //员工id
    private String employeeId;

    //客户名称
    private String customerName;

    //客户id
    private String customerId;

    //违规提示
    private String msg;


    //分析时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    @Transient
    private Date startTime;
    @Transient
    private Date endTime;

    // 在实体被持久化之前自动设置 createTime
    @PrePersist
    protected void onCreate() {
        this.createTime = new Date();
    }


}
