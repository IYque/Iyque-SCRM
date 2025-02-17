package cn.iyque.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "iyque_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueUser {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //员工名称
    private String name;


    //员工对应企微id
    private String userId;


    //员工部门名称，多个使用逗号隔开
    private String deptNames;


    //职务
    private String position;



}
