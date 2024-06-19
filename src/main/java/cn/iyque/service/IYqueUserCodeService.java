package cn.iyque.service;

import cn.iyque.domain.IYqueUserCode;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface IYqueUserCodeService {

    Page<IYqueUserCode> findAll(Pageable pageable);

    void save(IYqueUserCode product) throws Exception;

    void update(IYqueUserCode iYqueUserCode) throws Exception;

    IYqueUserCode findIYqueUserCodeById(Integer id);

    void batchDelete(Integer[] ids);

    void distributeUserCode(Integer id) throws Exception;

}
