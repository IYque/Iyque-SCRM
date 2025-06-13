package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.iyque.dao.IYqueCustomerSeasDao;
import cn.iyque.domain.IYqueCustomerSeasVo;
import cn.iyque.entity.IYqueCustomerSeas;
import cn.iyque.entity.IYqueUser;
import cn.iyque.service.IYqueCustomerSeasService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.IntStream;


@Service
@Slf4j
public class IYqueCustomerSeasServiceImpl implements IYqueCustomerSeasService {

    @Autowired
    IYqueCustomerSeasDao iYqueCustomerSeasDao;

    @Override
    public void importData(IYqueCustomerSeasVo seasVo, MultipartFile file) {

        try {
            EasyExcel.read(file.getInputStream(), IYqueCustomerSeas.class, new PageReadListener<IYqueCustomerSeas>(dataList -> {
                List<IYqueUser> allocateUsers = seasVo.getAllocateUsers();

                //公海客户平均分配给相关员工
                if(CollectionUtil.isNotEmpty(allocateUsers)){


                    int totalCustomers = dataList.size();
                    int totalEmployees = allocateUsers.size();

                    IntStream.range(0, totalCustomers).forEach(i -> {
                        int employeeIndex = i % totalEmployees;
                        dataList.get(i).setAllocateUserId(allocateUsers.get(employeeIndex).getUserId());
                        dataList.get(i).setAllocateUserName(allocateUsers.get(employeeIndex).getName());
                    });

                    iYqueCustomerSeasDao.saveAll(dataList);
                }
            })).sheet().doRead();
        }catch (Exception e){
            log.error("公海导入异常:"+e.getMessage());
        }


    }

    @Override
    public Page<IYqueCustomerSeas> findAll(IYqueCustomerSeas iYqueCustomerSeas, Pageable pageable) {
        Specification<IYqueCustomerSeas> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iYqueCustomerSeas.getPhoneNumber())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("phoneNumber")), "%" + iYqueCustomerSeas.getPhoneNumber().toLowerCase() + "%"));

        }
        if(StringUtils.isNotEmpty(iYqueCustomerSeas.getCustomerName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("customerName")), "%" + iYqueCustomerSeas.getCustomerName().toLowerCase() + "%"));

        }
        if(StringUtils.isNotEmpty(iYqueCustomerSeas.getAllocateUserName())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("allocateUserName")), "%" + iYqueCustomerSeas.getAllocateUserName().toLowerCase() + "%"));

        }

        if (iYqueCustomerSeas.getCurrentState() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("currentState")), iYqueCustomerSeas.getCurrentState()));
        }


        if (StringUtils.isNotEmpty(iYqueCustomerSeas.getAllocateUserId())) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("allocateUserId")), iYqueCustomerSeas.getAllocateUserId()));
        }

        return iYqueCustomerSeasDao.findAll(spec,pageable);
    }

    @Override
    public void batchDelete(Long[] ids) {
        iYqueCustomerSeasDao.deleteAllById(ListUtil.toList(ids));
    }

    @Override
    public void distribute(Long[] ids) throws Exception {

    }
}
