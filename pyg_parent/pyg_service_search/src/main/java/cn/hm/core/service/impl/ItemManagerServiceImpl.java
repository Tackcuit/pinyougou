package cn.hm.core.service.impl;

import cn.hm.core.dao.good.GoodsDao;
import cn.hm.core.dao.item.ItemDao;
import cn.hm.core.pojo.good.Goods;
import cn.hm.core.pojo.item.Item;
import cn.hm.core.pojo.item.ItemQuery;
import cn.hm.core.service.ItemManagerService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;

import java.util.List;
import java.util.Map;
@Service
public class ItemManagerServiceImpl implements ItemManagerService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;
    //有必要写一下注释

    /**
     * 当controller调用完更新商品激活状态之后调用此方法 可以将激活的商品库存信息添加到solr
     *
     * @param ids 商品ID集合
     * @return
     */
    @Override
    public int itemToSolr(Long[] ids) {
        int temp = 0;                       //返回用的的标识
        if (ids != null) {                  //判断形参为空
            for (Long id : ids) {
                Goods goods = goodsDao.selectByPrimaryKey(id);          //根据商品id查找商品信息
                if ("1".equals(goods.getAuditStatus())) {               //判断激活
                    ItemQuery itemQuery = new ItemQuery();                  //创建库存信息查询对象
                    ItemQuery.Criteria criteria = itemQuery.createCriteria();
                    criteria.andGoodsIdEqualTo(id);
                    List<Item> itemList = itemDao.selectByExample(itemQuery);           //查询库存信息
                    if (itemList != null) {                                     //判断是否有库存
                        for (Item item : itemList) {                                //遍历库存信息
                            if ("1".equals(item.getStatus())) {                     //判断库存信息激活状态
                                Map map = JSON.parseObject(item.getSpec(), Map.class);          //把库存信息的规格信息转化为map对象(规格信息本身在数据库里就是json字符串形式)
                                item.setSpecMap(map);                               //  把规格信息更新到item对象里面去
                            }
                        }
                        solrTemplate.saveBeans(itemList);               //添加到solr
                        solrTemplate.commit();                  //提交solr
                        temp++;                                             //更新返回标识
                    }
                }
            }
        }

        return temp;
    }


    /**
     * 当controller调用完商品删除方法之后调用此方法可以删除solr中的库存信息
     *
     * @param ids 删除库存信息的商品id
     * @return 影响的行数
     */
    @Override
    public int deleteItemFromSolr(Long[] ids) {
        int temp = 0;                                                                      //返回的标识
        if (ids != null) {                                                                 //判断
            for (Long id : ids) {                                                          //遍历
                Query query = new SimpleQuery();                                           //创建删除对象
                Criteria criteria = new Criteria("item_goodsid").is(id);        //创建删除信息
                query.addCriteria(criteria);                                               //添加删除信息
                solrTemplate.delete(query);                                                //删除
                solrTemplate.commit();                                                     //提交
                temp++;                                                                    //更新标识
            }
        }

        return temp;
    }
}
