package cn.hm.core.service;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.seller.Seller;

public interface SellerService {
    Seller findOne(String id);

    int delete(String[] ids);

    int update(Seller seller);

    int save(Seller seller);

    PageResult search(Seller seller, Integer page, Integer rows);

    int updateStatus(String sellerId, String status);
}
