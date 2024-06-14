package cn.iyque.service;

import cn.iyque.entity.IYqueUserCode;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IYqueUserCodeService {

    Page<IYqueUserCode> findAll(Pageable pageable);

    void save(IYqueUserCode product) throws Exception;

    void update(IYqueUserCode iYqueUserCode) throws Exception;

    IYqueUserCode findIYqueUserCodeById(Long id);

    void batchDelete(Long[] ids);

//    void distributeUserCode(Long id) throws WxErrorException;

}
