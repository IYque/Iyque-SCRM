package cn.iyque.converter;

import cn.hutool.core.util.StrUtil;
import cn.iyque.entity.IYqueMsgAnnex;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.external.msg.Attachment;
import me.chanjar.weixin.cp.bean.external.msg.Link;


/**
 * 图文转化器
 */
@Slf4j
public class LinkAttachmentConverter implements AttachmentConverter {
    @Override
    public Attachment convert(IYqueMsgAnnex annex) {
        IYqueMsgAnnex.Link link = annex.getLink();
        if(null != link && StrUtil.isNotEmpty(link.getTitle())
                && StrUtil.isNotEmpty(link.getUrl())){
            Attachment attachment=new Attachment();
            Link wLink=new Link();
            wLink.setTitle(link.getTitle());
            wLink.setDesc(link.getDesc());
            wLink.setPicUrl(link.getPicurl());
            wLink.setUrl(link.getUrl());
            attachment.setLink(wLink);
            return attachment;
        }else{
            log.error("图文消息标题或链接不可为空");
        }
        return null;
    }
}
