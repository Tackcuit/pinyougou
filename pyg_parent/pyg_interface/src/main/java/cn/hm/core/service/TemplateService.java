package cn.hm.core.service;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TemplateService {

    public PageResult findPage(TypeTemplate typeTemplate, Integer page, Integer rows);

    public int save(TypeTemplate typeTemplate);

    public int update(TypeTemplate typeTemplate);

    public int delete(Long[] ids);

    public TypeTemplate findOne(Long id);

    PageResult findAll();

    List<Map> selectOptionList();

    List<Map> findBySpecList(Long id);

//    String findBySpecList(Long id);
}
