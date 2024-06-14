package cn.iyque.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    //主键为id且自增
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    //默认欢迎语
    private String defaultContent;

    //欢迎语附件
    @Transient
    private List<IYqueMsgAnnex> annexLists;

}
