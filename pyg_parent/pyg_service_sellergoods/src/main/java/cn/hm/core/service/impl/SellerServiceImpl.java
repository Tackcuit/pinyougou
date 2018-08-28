package cn.hm.core.service.impl;

import cn.hm.core.dao.seller.SellerDao;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.seller.Seller;
import cn.hm.core.pojo.seller.SellerQuery;
import cn.hm.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerDao sellerDao;


    @Override
    public Seller findOne(String id) {
        return sellerDao.selectByPrimaryKey(id.trim());
    }

    @Override
    public int delete(String[] ids) {
        int temp = 0;
        if (ids != null) {
            for (String id : ids) {
                temp += sellerDao.deleteByPrimaryKey(id);
            }
        }
        return temp;
    }

    @Override
    public int update(Seller seller) {
        return sellerDao.updateByPrimaryKeySelective(seller);
    }

    @Override
    public int save(Seller seller) {
        return sellerDao.insertSelective(seller);
    }

    @Override
    public PageResult search(Seller seller, Integer page, Integer rows) {
        SellerQuery sellerQuery = new SellerQuery();
        if (seller != null) {
            SellerQuery.Criteria criteria = sellerQuery.createCriteria();
            if (seller.getName() != null && !"".equals(seller.getName().trim())) {
                criteria.andNameLike("%" + seller.getName() + "%");
            }
            if (seller.getNickName() != null && !"".equals(seller.getNickName().trim())) {
                criteria.andNameLike("%" + seller.getNickName() + "%");
            }
            if (seller.getStatus() != null && !"".equals(seller.getStatus())) {
                criteria.andStatusEqualTo(seller.getStatus());
            }
        }
        PageHelper.startPage(page, rows);
        Page<Seller> sellers = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        return new PageResult(sellers.getTotal(), sellers.getResult());
    }

    @Override
    public int updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        return update(seller);
    }
}
