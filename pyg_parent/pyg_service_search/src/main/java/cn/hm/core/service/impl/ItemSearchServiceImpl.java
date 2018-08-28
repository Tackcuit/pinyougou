package cn.hm.core.service.impl;

import cn.hm.core.pojo.item.Item;
import cn.hm.core.service.ItemSearchService;
import cn.hm.core.utils.Constants;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.stereotype.Service;
//这个应该是拉里巴巴dubbo的service

import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 简单的根据关键字搜索并返回结果
     *
     * @param paramMap 参数集
     * @return 结果集
     */
    @Override
    public Map<String, Object> search(Map paramMap) {
        Map<String, Object> map = new HashMap<>();
        String keywords = (String) paramMap.get("keywords");
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        ScoredPage<Item> items = solrTemplate.queryForPage(query, Item.class);
        map.put("rows", items.getContent());
        map.put("total", items.getTotalElements());
        map.put("totalPages", items.getTotalPages());
        return map;
    }

    /**
     * 根据关键字搜索并返回带高亮效果的结果集
     *
     * @param paramMap 参数集
     * @return 结果集
     */
    @Override
    public Map<String, Object> highLightsEarch(Map paramMap) {
        paramMap.put("keywords", String.valueOf(paramMap.get("keywords")).replaceAll(" ", ""));
        Map<String, Object> map = new HashMap<>();
        List<String> list = findCategoryList(paramMap);
        map.put("categoryList", list);
        map.putAll(findContent(paramMap));
        map.putAll(findBrandAndSpec(list.get(0)));
        return map;
    }


    public List<String> findCategoryList(Map paramMap) {
        ArrayList<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(paramMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        GroupResult<Item> category = items.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = category.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries) {
            list.add(groupEntry.getGroupValue());
        }
        return list;
    }


    public Map<String, Object> findContent(Map paramMap) {
        //返回的结果集
        Map<String, Object> map = new HashMap<>();
        //itemlist
        List<Item> itemList = new ArrayList<>();
        //高亮查询对象
        HighlightQuery highlightQuery = new SimpleHighlightQuery();
        //高亮参数集     高亮查询的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //高亮头
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //高亮尾
        highlightOptions.setSimplePostfix("</em>");
        //设置高亮参数
        highlightQuery.setHighlightOptions(highlightOptions);
        //查询参数      查询的关键字
        Criteria criteria = new Criteria("item_keywords").is((String) paramMap.get("keywords"));
        //设置查询参数
        highlightQuery.addCriteria(criteria);
        //分页
        highlightQuery.setOffset((paramMap.get("pageNo") != null && "".equals(paramMap.get("pageNo")) ? (Integer.parseInt(String.valueOf(paramMap.get("pageNo"))) - 1) * Integer.parseInt(String.valueOf(paramMap.get("pageSize"))) : 0));
        highlightQuery.setRows((paramMap.get("pageSize") != null && "".equals(paramMap.get("pageSize"))) ? Integer.parseInt(String.valueOf(paramMap.get("pageSize"))) : 40);

        //过滤
        //根据分类过滤
        String category = (String) paramMap.get("category");
        System.err.println(category);
        if (category != null && !"".equals(category)) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_category").is(category);
            filterQuery.addCriteria(criteria1);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //根据品牌过滤
        String brand = (String) paramMap.get("brand");
        if (brand != null && !"".equals(brand)) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_brand").is(brand);
            filterQuery.addCriteria(criteria1);
            highlightQuery.addFilterQuery(filterQuery);
        }
        //根据规格过滤
        String specStr = String.valueOf(paramMap.get("spec"));
        Map<String, String> specMap = JSON.parseObject(specStr, Map.class);
        if (specMap != null) {
            Set<String> set = specMap.keySet();
            for (String key : set) {
                //设置根据规格过滤
                FilterQuery specQuery = new SimpleFilterQuery();
                Criteria specCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                //将过滤查询条件放入过滤对象中
                specQuery.addCriteria(specCriteria);
                //将过滤对象放入查询对象中
                highlightQuery.addFilterQuery(specQuery);
            }
        }

        String price = String.valueOf(paramMap.get("price"));
        if (price != null && !"".equals(price)) {
            String[] prices = price.split("-");
            SimpleFilterQuery priceFilterQuery = new SimpleFilterQuery();
            if (prices[0] != null && !"0".equals(prices[0])) {
                Criteria item_price = new Criteria("item_price").greaterThanEqual(prices[0]);
                priceFilterQuery.addCriteria(item_price);
            }
            if (prices[1] != null && !"*".equals(prices[1])) {
                Criteria item_price = new Criteria("item_price").lessThanEqual(prices[1]);
                priceFilterQuery.addCriteria(item_price);
            }
            highlightQuery.addFilterQuery(priceFilterQuery);
        }
        String sort = (String) paramMap.get("sort");
        String sortField = (String) paramMap.get("sortField");
        if (sort != null && sortField != null && !"".equals(sort) && !"".equals(sortField)) {
            if ("ASC".equals(sort.trim())) {
                Sort orders = new Sort(Sort.Direction.ASC, "item_" + sortField.trim());
                highlightQuery.addSort(orders);
            }
            if ("DESC".equals(sort.trim())) {
                Sort orders = new Sort(Sort.Direction.DESC, "item_" + sortField.trim());
                highlightQuery.addSort(orders);
            }

        }
        //获取查询的结果
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(highlightQuery, Item.class);
        if (items != null) {
            //遍历  把高亮的替换不高亮的
            for (HighlightEntry<Item> entry : items.getHighlighted()) {
                Item entity = entry.getEntity();
                try {
                    entity.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
                } catch (Exception e) {

                }
                itemList.add(entity);
            }
        }
        map.put("rows", itemList);
        map.put("total", items.getTotalElements());
        map.put("totalPages", items.getTotalPages());
        return map;
    }

    private Map<String, Object> findBrandAndSpec(String categoryName) {
        Long templateId = (Long) redisTemplate.boundHashOps(Constants.CATEGORY_REDIS).get(categoryName);
        Map<String, Object> resultMap = new HashMap<>();
        /**
         * 获取品牌数据集合
         */
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps(Constants.BRAND_REDIS).get(templateId);
        resultMap.put("brandList", brandList);

        /**
         * 获取规格数据集合
         */
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps(Constants.SPEC_REDIS).get(templateId);
        resultMap.put("specList", specList);
        return resultMap;
    }

}
