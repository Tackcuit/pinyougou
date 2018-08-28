package cn.hm.core.service;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<Brand> getAll();

    Long getCount();

    PageResult getPage(Integer page, Integer rows);

    int save(Brand brand);

    int update(Brand brand);

    int delete(Long[] ids);

    Brand getOne(Long id);

    PageResult searchPage(Brand brand, Integer page, Integer rows);

    List<Map> selectOptionList();
}
