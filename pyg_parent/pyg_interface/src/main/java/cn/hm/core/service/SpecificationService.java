package cn.hm.core.service;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.SpecEntity;
import cn.hm.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {

    public List<Specification> findAll();

    public PageResult Search(Specification specification, Integer page, Integer rows);

    public int save(SpecEntity specEntity);

    public int update(SpecEntity specEntity);

    public int delete(Long[] ids);

    public SpecEntity findOne(Long id);

    public List<Map> selectOptionList();

}
