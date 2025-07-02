package cn.iyque.dao;

import cn.iyque.entity.IYqueCategory;
import jdk.internal.net.http.common.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IYqueCategoryDao extends JpaRepository<IYqueCategory,Long>, JpaSpecificationExecutor<IYqueCategory> {


}
