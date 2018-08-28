package cn.hm.core.utils;

import cn.hm.core.dao.item.ItemCatDao;
import cn.hm.core.dao.specification.SpecificationOptionDao;
import cn.hm.core.dao.template.TypeTemplateDao;
import cn.hm.core.pojo.item.ItemCat;
import cn.hm.core.pojo.specification.SpecificationOption;
import cn.hm.core.pojo.specification.SpecificationOptionQuery;
import cn.hm.core.pojo.template.TypeTemplate;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RedisUtil {

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TypeTemplateDao typeTemplateDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    public void ItemCatToRedis() {
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        if (itemCats != null) {
            for (ItemCat cat : itemCats) {
                //key是分类名称, value是模板id
                redisTemplate.boundHashOps(Constants.CATEGORY_REDIS).put(cat.getName(), cat.getTypeId());
            }
        }
    }

    public void TemplateToRedis() {
        List<TypeTemplate> templateList = typeTemplateDao.selectByExample(null);
        if (templateList != null) {
            for (TypeTemplate temp : templateList) {
                /**
                 * 缓存品牌集合数据
                 */
                List<Map> brandList = JSON.parseArray(temp.getBrandIds(), Map.class);
                redisTemplate.boundHashOps(Constants.BRAND_REDIS).put(temp.getId(), brandList);

                /**
                 * 缓存规格集合数据
                 */
                List<Map> specList = findSpecList(temp.getId());
                redisTemplate.boundHashOps(Constants.SPEC_REDIS).put(temp.getId(), specList);
            }
        }
    }

    public List<Map> findSpecList(Long id) {
        //1. 根据模板id, 获取模板数据
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //2. 将获取到的规格的基本数据, 转换成对象, 原来是json字符串
        List<Map> maps = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        //3. 根据规格id获取规格选项数据, 并且拼接到这个规格集合中
        if (maps != null) {
            for (Map map : maps) {
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                criteria.andSpecIdEqualTo(Long.parseLong(String.valueOf(map.get("id"))));
                List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
                map.put("options", options);
            }
        }
        return maps;
    }

}
