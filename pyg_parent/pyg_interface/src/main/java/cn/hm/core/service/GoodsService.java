package cn.hm.core.service;

import cn.hm.core.pojo.entity.GoodsEntity;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.good.Goods;

import java.util.List;

public interface GoodsService {
    int save(GoodsEntity goodsEntity);

    int update(GoodsEntity goodsEntity);

    PageResult search(Goods goods, Integer page, Integer rows);

    List<Goods> findAll();

    int delete(Long[] ids);

    GoodsEntity findOne(Long id);

    int updateStatus(Long[] ids, String status);
}
