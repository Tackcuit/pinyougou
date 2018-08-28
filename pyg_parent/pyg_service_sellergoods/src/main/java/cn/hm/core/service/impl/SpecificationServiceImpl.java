package cn.hm.core.service.impl;

import cn.hm.core.dao.specification.SpecificationDao;
import cn.hm.core.dao.specification.SpecificationOptionDao;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.SpecEntity;
import cn.hm.core.pojo.specification.Specification;
import cn.hm.core.pojo.specification.SpecificationOption;
import cn.hm.core.pojo.specification.SpecificationOptionQuery;
import cn.hm.core.pojo.specification.SpecificationQuery;
import cn.hm.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationDao specificationDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public List<Specification> findAll() {
        return specificationDao.selectByExample(null);
    }

    @Override
    public PageResult Search(Specification specification, Integer page, Integer rows) {
        SpecificationQuery specificationQuery = new SpecificationQuery();
        if (specification != null) {
            SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();
            if (specification.getSpecName() != null && !"".equals(specification.getSpecName())) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
        }
        PageHelper.startPage(page, rows);
        Page<Specification> specificationPage = (Page<Specification>) specificationDao.selectByExample(specificationQuery);
        return new PageResult(specificationPage.getTotal(), specificationPage.getResult());
    }

    @Override
    public int save(SpecEntity specEntity) {
        int temp = 0;
        temp += specificationDao.insertSelective(specEntity.getSpecification());
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption specificationOption : specEntity.getSpecificationOptionList()) {
                specificationOption.setSpecId(specEntity.getSpecification().getId());
                temp += specificationOptionDao.insertSelective(specificationOption);
            }
        }
        return temp;
    }

    @Override
    public int update(SpecEntity specEntity) {
        int temp = 0;
        temp += specificationDao.updateByPrimaryKeySelective(specEntity.getSpecification());
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpecification().getId());
        temp += specificationOptionDao.deleteByExample(specificationOptionQuery);
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption specificationOption : specEntity.getSpecificationOptionList()) {
                specificationOption.setSpecId(specEntity.getSpecification().getId());
                temp += specificationOptionDao.insertSelective(specificationOption);
            }
        }
        return temp;
    }

    @Override
    public int delete(Long[] ids) {
        int temp = 0;
        if (ids != null) {
            for (Long id : ids) {
                temp += specificationDao.deleteByPrimaryKey(id);
                SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
                criteria.andSpecIdEqualTo(id);
                temp += specificationOptionDao.deleteByExample(specificationOptionQuery);
            }
        }
        return temp;
    }

    @Override
    public SpecEntity findOne(Long id) {
        Specification specification = specificationDao.selectByPrimaryKey(id);
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
        criteria.andSpecIdEqualTo(specification.getId());
        List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);
        return new SpecEntity(specification, specificationOptions);
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }

}
