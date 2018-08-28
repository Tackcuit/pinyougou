package cn.hm.core.service.impl;

import cn.hm.core.dao.address.AddressDao;
import cn.hm.core.pojo.address.Address;
import cn.hm.core.pojo.address.AddressQuery;
import cn.hm.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressDao addressDao;

    @Override
    public List<Address> findListByUserId(String userId) {
        AddressQuery addressQuery = new AddressQuery();
        AddressQuery.Criteria criteria = addressQuery.createCriteria();
        criteria.andUserIdEqualTo(userId);
        return addressDao.selectByExample(addressQuery);
    }
}
