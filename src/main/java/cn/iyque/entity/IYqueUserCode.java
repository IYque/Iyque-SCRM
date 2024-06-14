package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 外部联系人活码实体
 */
@Entity(name = "iyque_user_code")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "delFlag = 0")
public class IYqueUserCode {

    //主键为id且自增
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    //名称
    private String codeName;

    //员工id,多个使用逗号隔开
    private String userId;

    //员工名称,多个使用逗号隔开
    private String userName;

    //是否免验证:true:免验证 false:需验证
    private Boolean skipVerify;

    //是否可重复添加: true:可重复添加 false:不可重复添加
    private Boolean isExclusive;

    //标签id,多个使用逗号隔开
    private String tagId;

    //标签名,多个使用逗号隔开
    private String tagName;

    //欢迎语
    private String weclomeMsg;

    //渠道标识
    private String codeState;

    //活码地址
    private String codeUrl;

    //联系方式的配置id
    private String configId;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    //是否删除标识
    private Integer delFlag;

    @Transient
    private List<IYqueMsgAnnex> annexLists;

    @PrePersist
    @PreUpdate
    private void setDefaultDelFlag() {
        if (this.delFlag == null) {
            this.delFlag = 0;
        }
    }
}
