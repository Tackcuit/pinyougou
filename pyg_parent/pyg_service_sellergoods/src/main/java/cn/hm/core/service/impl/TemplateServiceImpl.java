package cn.hm.core.service.impl;

import cn.hm.core.dao.specification.SpecificationOptionDao;
import cn.hm.core.dao.template.TypeTemplateDao;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.specification.SpecificationOption;
import cn.hm.core.pojo.specification.SpecificationOptionQuery;
import cn.hm.core.pojo.template.TypeTemplate;
import cn.hm.core.pojo.template.TypeTemplateQuery;
import cn.hm.core.service.TemplateService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult findPage(TypeTemplate typeTemplate, Integer page, Integer rows) {

        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        if (typeTemplate != null) {
            TypeTemplateQuery.Criteria criteria = typeTemplateQuery.createCriteria();
            if (typeTemplate.getName() != null) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
        }
        PageHelper.startPage(page, rows);
        Page<TypeTemplate> typeTemplatepage = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        return new PageResult(typeTemplatepage.getTotal(), typeTemplatepage.getResult());

    }

    @Override
    public int save(TypeTemplate typeTemplate) {
        return typeTemplateDao.insertSelective(typeTemplate);
    }

    @Override
    public int update(TypeTemplate typeTemplate) {
        return typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public int delete(Long[] ids) {
        int temp = 0;
        if (ids != null) {
            for (Long id : ids) {
                temp += typeTemplateDao.deleteByPrimaryKey(id);
            }
        }
        return temp;
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }


    @Override
    public PageResult findAll() {
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        return new PageResult((long) typeTemplates.size(), typeTemplates);
    }

    @Override
    public List<Map> selectOptionList() {
        return typeTemplateDao.selectOptionList();
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate one = findOne(id);
        List<Map> maps = JSON.parseArray(one.getSpecIds(), Map.class);
        if (maps != null) {
            for (Map map : maps) {
                Long sid = Long.parseLong(String.valueOf(map.get("id")));
                SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
                criteria.andSpecIdEqualTo(sid);
                List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);
                map.put("options", specificationOptions);
            }
        }
        return maps;
    }
}
