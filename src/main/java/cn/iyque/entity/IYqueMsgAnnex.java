package cn.iyque.entity;


import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;


/**
 * 欢迎语附件
 */
@Entity(name = "iyque_msg_annex")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueMsgAnnex {
    //主键为id且自增
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    //欢迎语id
    private Long msgId;

    //附件类型
    private String msgtype;

    //附件字符串
    private String annexContent;

    @Transient
    private Image image;

    @Transient
    private Link link;

    @Transient
    private Miniprogram miniprogram;

    @Transient
    private Video video;

    @Transient
    private File file;

    @PostLoad
    public void postLoad() {
        if (MsgType.MSG_TYPE_IMAGE.equals(msgtype)) {
            image = JSONUtil.toBean(annexContent,Image.class);
        } else if (MsgType.MSG_TYPE_LINK.equals(msgtype)) {
            link = JSONUtil.toBean(annexContent,Link.class);
        } else if (MsgType.MSG_TYPE_MINIPROGRAM.equals(msgtype)) {
            miniprogram = JSONUtil.toBean(annexContent,Miniprogram.class);
        } else if (MsgType.MSG_TYPE_VIDES.equals(msgtype)) {
            video = JSONUtil.toBean(annexContent,Video.class);
        } else if (MsgType.MSG_TYPE_FILE.equals(msgtype)) {
            file = JSONUtil.toBean(annexContent,File.class);
        }
    }

    @PrePersist
    public void prePersist() {
        if (MsgType.MSG_TYPE_IMAGE.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(image);
        } else if (MsgType.MSG_TYPE_LINK.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(link);
        } else if (MsgType.MSG_TYPE_MINIPROGRAM.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(miniprogram);
        } else if (MsgType.MSG_TYPE_VIDES.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(video);
        } else if (MsgType.MSG_TYPE_FILE.equals(msgtype)) {
            annexContent = JSONUtil.toJsonStr(file);
        }
    }


    //附件类型
    @Data
    public static class MsgType{
        //图片
        public final static String MSG_TYPE_IMAGE="image";
        //链接
        public final  static String MSG_TYPE_LINK="link";
        //小程序
        public final static String MSG_TYPE_MINIPROGRAM="miniprogram";
        //视频
        public final static String MSG_TYPE_VIDES="video";
        //文件
        public final static String MSG_TYPE_FILE="file";
    }


    //图片
    @Data
    public class Image{
        //图片地址
        private String picUrl;
    }


    //链接
    @Data
    public class Link{
        //链接标题
        private String title;
        //链接封面
        private String picurl;
        //链接描述
        private String desc;
        //链接跳转地址
        private String url;
    }

    //小程序
    @Data
    public class Miniprogram{
        //小程序标题
        private String title;
        //小程序封面地址
        private String picurl;
        //小程序id
        private String appid;
        //小程序页面
        private String page;
    }


    //视频
    @Data
    public class Video{
        //视频地址
        private String videoUrl;
    }


    //文件
    @Data
    public class File{
        //文件地址
        private String fileUrl;
    }




}
