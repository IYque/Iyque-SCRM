package cn.iyque.dao;

import cn.iyque.entity.IYqueUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IYqueUserDao extends JpaRepository<IYqueUser,Long> ,JpaSpecificationExecutor<IYqueUser> {


    List<IYqueUser> findIYqueUserByUserId(String userId);


    List<IYqueUser> findByUserId(String userId);
}
