package cn.hm.core.service.impl;

import cn.hm.core.dao.ad.ContentCategoryDao;
import cn.hm.core.pojo.ad.ContentCategory;
import cn.hm.core.pojo.ad.ContentCategoryQuery;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.service.ContentCateoryService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentCateoryServiceImpl implements ContentCateoryService {

    @Autowired
    private ContentCategoryDao contentCategoryDao;

    @Override
    public int save(ContentCategory contentCategory) {
        return contentCategoryDao.insertSelective(contentCategory);
    }

    @Override
    public int delete(Long[] ids) {
        int temp = 0;
        if (ids != null) {
            for (Long id : ids) {
                temp += contentCategoryDao.deleteByPrimaryKey(id);
            }
        }
        return temp;
    }

    @Override
    public int update(ContentCategory contentCategory) {
        return contentCategoryDao.updateByPrimaryKeySelective(contentCategory);
    }

    @Override
    public PageResult search(ContentCategory contentCategory, Integer page, Integer rows) {

        ContentCategoryQuery contentCategoryQuery = new ContentCategoryQuery();
        if (contentCategoryDao != null) {
            ContentCategoryQuery.Criteria criteria = contentCategoryQuery.createCriteria();
            if (contentCategory.getName() != null && !"".equals(contentCategory.getName())) {
                criteria.andNameLike("%" + contentCategory.getName() + "%");
            }

        }
        PageHelper.startPage(page, rows);
        Page<ContentCategory> contentCategoryPage = (Page<ContentCategory>) contentCategoryDao.selectByExample(contentCategoryQuery);
        return new PageResult(contentCategoryPage.getTotal(), contentCategoryPage.getResult());
    }

    @Override
    public List<ContentCategory> findAll() {
        return contentCategoryDao.selectByExample(null);
    }

    @Override
    public ContentCategory findOne(Long id) {
        return contentCategoryDao.selectByPrimaryKey(id);
    }
}
