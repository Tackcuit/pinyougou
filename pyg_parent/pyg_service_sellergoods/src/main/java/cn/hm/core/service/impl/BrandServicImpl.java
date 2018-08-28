package cn.hm.core.service.impl;

import cn.hm.core.dao.good.BrandDao;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.good.Brand;
import cn.hm.core.pojo.good.BrandQuery;
import cn.hm.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServicImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;

    @Override
    public List<Brand> getAll() {
        return brandDao.selectByExample(null);
    }

    @Override
    public Long getCount() {
        return null;
    }

    @Override
    public PageResult getPage(Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        Page<Brand> pageBrand = (Page<Brand>) brandDao.selectByExample(null);
        return new PageResult(pageBrand.getTotal(), pageBrand.getResult());
    }

    @Override
    public int save(Brand brand) {
        return brandDao.insertSelective(brand);
    }

    @Override
    public int update(Brand brand) {
        return brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public int delete(Long[] ids) {
        int temp = 0;
        if (ids != null) {
            for (Long id : ids) {
                temp += brandDao.deleteByPrimaryKey(id);
            }
        }
        return temp;
    }

    @Override
    public Brand getOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public PageResult searchPage(Brand brand, Integer page, Integer rows) {
        //创建查询条件对象
        BrandQuery brandQuery = new BrandQuery();
        if (brand != null) {
            //创建where条件对象
            BrandQuery.Criteria criteria = brandQuery.createCriteria();
            //拼接名称模糊查询条件
            if (brand.getName() != null && !"".equals(brand.getName())) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            //拼接根据首字母查询
            if (brand.getFirstChar() != null && !"".equals(brand.getFirstChar())) {
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        //告诉分页插件当前页, 以及每页需要展示多少条数据
        PageHelper.startPage(page, rows);
        //将数据库中查询出来的当前表的所有数据返回给分页插件
        Page<Brand> pageList = (Page<Brand>) brandDao.selectByExample(brandQuery);
        //从分页插件中获取当前页需要展示的数据
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }

}
