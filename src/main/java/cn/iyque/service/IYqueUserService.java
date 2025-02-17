package cn.iyque.service;

import cn.iyque.entity.IYqueUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IYqueUserService {

    /**
     * 同步组织架构可见范围的员工
     */
    void synchIyqueUser();


    /**
     * 获取所有成员
     * @return
     */
    List<IYqueUser> findIYqueUser();


    /**
     * 分页获取成员数据
     * @param name
     * @param pageable
     * @return
     */
    Page<IYqueUser> findIYqueUserPage(String name,Pageable pageable);
}
