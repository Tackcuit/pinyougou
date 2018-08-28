package cn.hm.core.service;

import cn.hm.core.pojo.ad.Content;
import cn.hm.core.pojo.entity.PageResult;

import java.util.List;

public interface ContentService {
    List<Content> findAll();

    PageResult findPage(Object o, Integer page, Integer rows);

    int save(Content content);

    Content findOne(Long id);

    int update(Content content);

    int delete(Long[] ids);

    List<Content> findByCategoryId(Long categoryId);


}
