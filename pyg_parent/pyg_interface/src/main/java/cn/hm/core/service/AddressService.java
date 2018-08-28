package cn.hm.core.service;

import cn.hm.core.pojo.address.Address;

import java.util.List;

public interface AddressService {
    public List<Address> findListByUserId(String userId );
}
