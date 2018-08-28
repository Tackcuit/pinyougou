package cn.hm.core.service.impl;

import cn.hm.core.dao.item.ItemCatDao;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.item.ItemCat;
import cn.hm.core.pojo.item.ItemCatQuery;
import cn.hm.core.service.ItemCatService;
import cn.hm.core.utils.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.aspectj.weaver.ast.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ItemCat> findByParentId(Long id) {
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();
        criteria.andParentIdEqualTo(id);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public int save(ItemCat itemCat) {
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(Constants.CATEGORY_REDIS);
        if (boundHashOperations.get(itemCat.getName()) != null) {
            boundHashOperations.delete(itemCat.getName());
        }
        boundHashOperations.put(itemCat.getName(), itemCat.getTypeId());
        return itemCatDao.insertSelective(itemCat);
    }

    @Override
    public int update(ItemCat itemCat) {
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(Constants.CATEGORY_REDIS);
        if (boundHashOperations.get(itemCat.getName()) != null) {
            boundHashOperations.delete(itemCat.getName());
        }
        boundHashOperations.put(itemCat.getName(), itemCat.getTypeId());
        return itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    @Override
    public int delete(Long[] ids) {
        return deletes(ids);
    }

    @Override
    public PageResult search(ItemCat itemCat, Integer page, Integer rows) {
        Page<ItemCat> itemCatPage = (Page<ItemCat>) searchs(itemCat, page, rows);
        return new PageResult(itemCatPage.getTotal(), itemCatPage.getResult());
    }

    public List<ItemCat> searchs(ItemCat itemCat, Integer page, Integer rows) {

        ItemCatQuery itemCatQuery = new ItemCatQuery();
        if (itemCat != null) {
            ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();
            if (itemCat.getName() != null && "".equals(itemCat.getName())) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }
            if (itemCat.getParentId() != null) {
                criteria.andParentIdEqualTo(itemCat.getParentId());
            }
            if (itemCat.getTypeId() != null) {
                criteria.andTypeIdEqualTo(itemCat.getTypeId());
            }
        }
        PageHelper.startPage(page, rows);
        itemCatDao.selectByExample(itemCatQuery);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    public int deletes(Long[] ids) {
        int temp = 0;
        if (ids != null) {
            for (Long id : ids) {
                List<ItemCat> byParentId = findByParentId(id);
                if (byParentId != null) {
                    Long[] longs = new Long[byParentId.size()];
                    for (int i = 0; i < byParentId.size(); i++) {
                        longs[i] = byParentId.get(i).getId();
                    }
                    temp += deletes(longs);
                }
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(id);
                BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(Constants.CATEGORY_REDIS);
                if (boundHashOperations.get(itemCat.getName()) != null) {
                    boundHashOperations.delete(itemCat.getName());
                }
                temp += itemCatDao.deleteByPrimaryKey(id);
            }
        }
        return temp;
    }

    //    public int deletes(List<ItemCat> itemCatList) {
//        int temp = 0;
//        if (itemCatList != null) {
//            for (ItemCat itemCat : itemCatList) {
//                temp += deletes(findByParentId(itemCat.getId()));
//            }
//        }
//        return temp;
//    }

}
