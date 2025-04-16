package cn.iyque.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;


/**
 * 客服
 */
@Entity(name = "iyque_kf")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueKf {

    @Id
    @GeneratedValue(generator = "snowflakeIdGenerator")
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "cn.iyque.utils.SnowFlakeUtils"
    )
    private Long id;


    //客服id
    private String openKfid;


    //客服名称
    private String kfName;


    //客服链接
    private String kfUrl;


    //客服链接二维码
    private String kfQrUrl;


    //头像
    private String kfPicUrl;

    //欢迎语
    private String welcomeMsg;



    //知识库id,多个使用逗号隔开(当前暂时只支持单个)
    private String kId;

    //知识库名称,多个使用逗号隔开(当前暂时只支持单个)
    private String kName;

    //如果ai知识库匹配不到,转接方式:1:文字;2:直接转人工客服;3:发送外部联系人二维码
    private  Integer switchType;

    //当转接方式为1的时候,转接文字
    private String switchText;

    //当转接方式为2的时候转接人id(目前暂时只支持单个)
    private String switchUserIds;


    //当转接方式为2的时候转接人名称(目前暂时只支持单个)
    private String switchUserNames;

    //当转接方式为3的时候,外部联系人二维码
    private String swichQrUrl;


    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private  Date updateTime;





}
