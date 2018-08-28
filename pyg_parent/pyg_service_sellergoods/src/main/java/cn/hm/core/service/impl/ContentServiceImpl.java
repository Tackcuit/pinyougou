package cn.hm.core.service.impl;

import cn.hm.core.dao.ad.ContentDao;
import cn.hm.core.pojo.ad.Content;
import cn.hm.core.pojo.ad.ContentQuery;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Content> findAll() {
        return contentDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Object o, Integer page, Integer rows) {
        ContentQuery query = new ContentQuery();
        //根据字段中的内容排序, 这里是降序
        query.setOrderByClause("sort_order desc");
        PageHelper.startPage(page, rows);
        Page<Content> pageList = (Page<Content>) contentDao.selectByExample(query);
        return new PageResult(pageList.getTotal(), pageList.getResult());
    }

    @Override
    public int save(Content content) {
        int i = contentDao.insertSelective(content);
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        return i;
    }

    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public int update(Content content) {
        Long categoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
        redisTemplate.boundHashOps("content").delete(categoryId);
        int i = contentDao.updateByPrimaryKeySelective(content);
        //如果分类ID发生了修改,清除修改后的分类ID的缓存
        if (categoryId.longValue() != content.getCategoryId().longValue()) {
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }
        return i;
    }

    @Override
    public int delete(Long[] ids) {
        int temp = 0;
        if (ids != null) {
            for (Long id : ids) {
                Long categoryId = contentDao.selectByPrimaryKey(id).getCategoryId();//广告分类ID
                redisTemplate.boundHashOps("content").delete(categoryId);
                temp += contentDao.deleteByPrimaryKey(id);
            }
        }
        return temp;
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        List<Content> contents = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
        if (contents == null) {
            System.err.println("-------------------------------从数据库读取图片地址-------------------------------");
            ContentQuery query = new ContentQuery();
            //按照排序字段降序排列
            query.setOrderByClause("sort_order desc");
            ContentQuery.Criteria criteria = query.createCriteria();
            //根据外键查询
            criteria.andCategoryIdEqualTo(categoryId);
            //查询状态为1的
            criteria.andStatusEqualTo("1");
            contents = contentDao.selectByExample(query);
            redisTemplate.boundHashOps("content").put(categoryId, contents);
        } else {
            System.err.println("-------------------------------从Redis读取图片地址-------------------------------");
        }
        return contents;
    }
}
