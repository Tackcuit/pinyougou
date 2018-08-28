package cn.hm.core.service;

import cn.hm.core.pojo.ad.ContentCategory;
import cn.hm.core.pojo.entity.PageResult;

import java.util.List;

public interface ContentCateoryService {
    int save(ContentCategory contentCategory);

    int delete(Long[] ids);

    int update(ContentCategory contentCategory);

    PageResult search(ContentCategory contentCategory, Integer page, Integer rows);

    List<ContentCategory> findAll();

    ContentCategory findOne(Long id);
}
