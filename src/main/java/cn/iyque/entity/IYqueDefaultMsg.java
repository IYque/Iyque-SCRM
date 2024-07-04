package cn.iyque.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;


/**
 * 默认欢迎语
 */
@Entity(name = "iyque_default_msg")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueDefaultMsg {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;

    //默认欢迎语
    private String defaultContent;

    //欢迎语附件
    @Transient
    private List<IYqueMsgAnnex> annexLists;

}
