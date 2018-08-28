package cn.hm.core.service;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.item.ItemCat;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long id);

    ItemCat findOne(Long id);

    int save(@RequestParam ItemCat itemCat);

    int update(@RequestParam ItemCat itemCat);

    int delete(Long[] ids);

    PageResult search(ItemCat itemCat, Integer page, Integer rows);

    int getCount();

    List<ItemCat> findAll();
}
