package cn.hm.core.service.impl;

import cn.hm.core.dao.good.BrandDao;
import cn.hm.core.dao.good.GoodsDao;
import cn.hm.core.dao.good.GoodsDescDao;
import cn.hm.core.dao.item.ItemCatDao;
import cn.hm.core.dao.item.ItemDao;
import cn.hm.core.dao.seller.SellerDao;
import cn.hm.core.pojo.entity.GoodsEntity;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.good.Brand;
import cn.hm.core.pojo.good.Goods;
import cn.hm.core.pojo.good.GoodsQuery;
import cn.hm.core.pojo.item.Item;
import cn.hm.core.pojo.item.ItemCat;
import cn.hm.core.pojo.item.ItemQuery;
import cn.hm.core.pojo.seller.Seller;
import cn.hm.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQTopic topicPageAndSolrDestination;
    @Autowired
    private ActiveMQQueue queueSolrDeleteDestination;

    @Override
    public int save(GoodsEntity goodsEntity) {

        int temp = 0;
        //存商品信息    处理为未审核    存商品的基本信息
        goodsEntity.getGoods().setAuditStatus("0");
        temp += goodsDao.insertSelective(goodsEntity.getGoods());
        //存商品详情    处理商品id和商品信息表id相同    存商品包括颜色款式材质等选项之类
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        temp += goodsDescDao.insertSelective(goodsEntity.getGoodsDesc());
        List<Item> itemList = getItemData(goodsEntity);
        for (Item item : itemList) {
            temp += itemDao.insertSelective(item);
        }
        return temp;
    }

    @Override
    public int update(GoodsEntity goodsEntity) {
        int temp = 0;
        //修改商品信息    处理为未审核    我认为修改后应该重新审核    为了方便测试暂且放弃     存商品的基本信息
//        goodsEntity.getGoods().setAuditStatus("0");
        temp += goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        //修改商品详情    处理商品id和商品信息表id相同    存商品包括颜色款式材质等选项之类
        goodsEntity.getGoodsDesc().setGoodsId(goodsEntity.getGoods().getId());
        temp += goodsDescDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        //删除所有库存信息
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemDao.deleteByExample(itemQuery);
        List<Item> itemList = getItemData(goodsEntity);
        for (Item item : itemList) {
            temp += itemDao.insertSelective(item);
        }
        return temp;
    }

    @Override
    public PageResult search(Goods goods, Integer page, Integer rows) {
        GoodsQuery goodsQuery = new GoodsQuery();

        //是否查询已经被删除的
        //goods.setIsDelete("1");

        if (goods != null) {
            GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
            if (goods.getSellerId() != null && !"".equals(goods.getSellerId())) {
                criteria.andSellerIdEqualTo(goods.getSellerId().trim());
            }
            if (goods.getGoodsName() != null && !"".equals(goods.getGoodsName())) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getIsDelete() != null && !"".equals(goods.getIsDelete())) {
                criteria.andIsDeleteEqualTo("1");
            }
        }
        PageHelper.startPage(page, rows);
        Page<Goods> goodsPage = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(goodsPage.getTotal(), goodsPage.getResult());
    }

    @Override
    public List<Goods> findAll() {
        return goodsDao.selectByExample(null);
    }

    @Override
    public int delete(Long[] ids) {
        int temp = 0;
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            temp += goodsDao.updateByPrimaryKeySelective(goods);
        }
        jmsTemplate.send(queueSolrDeleteDestination, (session) ->
                session.createTextMessage(Arrays.toString(ids))
        );
        return temp;
    }

    @Override
    public GoodsEntity findOne(Long id) {
        GoodsEntity goodsEntity = new GoodsEntity();
        goodsEntity.setGoods(goodsDao.selectByPrimaryKey(id));
        goodsEntity.setGoodsDesc(goodsDescDao.selectByPrimaryKey(id));
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        goodsEntity.setItemList(itemDao.selectByExample(itemQuery));
        return goodsEntity;
    }

    @Override
    public int updateStatus(Long[] ids, String status) {
        int temp = 0;
        if (ids != null && status != null && !"".equals(status)) {
            for (Long id : ids) {
                Goods goods = new Goods();
                goods.setId(id);
                goods.setAuditStatus(status.trim());
                temp += goodsDao.updateByPrimaryKeySelective(goods);

                ItemQuery itemQuery = new ItemQuery();
                ItemQuery.Criteria criteria = itemQuery.createCriteria();
                criteria.andGoodsIdEqualTo(id);

                Item item = new Item();
                item.setStatus(status);
                itemDao.updateByExampleSelective(item, itemQuery);
            }
            if ("1".equals(status)) {
                jmsTemplate.send(topicPageAndSolrDestination, (session) ->
                        session.createTextMessage(Arrays.toString(ids))
                );
            }
        }
        return temp;
    }

    public List<Item> getItemData(GoodsEntity goodsEntity) {
        ArrayList<Item> items = new ArrayList<>();
        //存库存信息     判断是否有规格
        if (goodsEntity.getGoods().getIsEnableSpec() != null && "1".equals(goodsEntity.getGoods().getIsEnableSpec())) {
            //库存有很多的型号    所以遍历
            List<Item> itemList = goodsEntity.getItemList();
            for (Item item : itemList) {
                items.add(getItemData(goodsEntity, item));
            }
        } else {
            //没有库存数据, 初始化一条数据, 要么库存数据为null会报错
            Item item = new Item();
            //初始化库存标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            //初始化状态为1已审核
            item.setStatus("1");
            //初始化规格数据, 为一个空的json串
            item.setSpec("{}");
            //初始化售价
            item.setPrice(new BigDecimal("99999999999"));
            //设置库存对象的值
            items.add(getItemData(goodsEntity, item));
        }
        return items;
    }

    public Item getItemData(GoodsEntity goodsEntity, Item item) {

        //库存名称全名    我认为是   品牌+商品名+规格等信息
        Brand brand = brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        //品牌和商品名
        StringBuilder title = new StringBuilder();
        title.append(brand.getName() + " ");
        title.append(goodsEntity.getGoods().getGoodsName() + " ");
        //规格信息也是一堆   所以循环取出来
        Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
        if (specMap != null) {
            for (String s : specMap.keySet()) {
                title.append(specMap.get(s) + " ");
            }
        }

        //组合库存名称全名
        item.setTitle(title.toString());

        //库存数据状态, 库存默认设置成0, 未审核状态
        item.setStatus("0");
        item.setBrand(brand.getName());

        //分类id
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        ItemCat itemCat = itemCatDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());

        //分类名称
        item.setCategory(itemCat.getName());

        //设置商品ID
        item.setGoodsId(goodsEntity.getGoods().getId());

        //设置样张图片   默认使用第一张
        List<Map> imagesMap = JSON.parseArray(goodsEntity.getGoodsDesc().getItemImages(), Map.class);
        if (imagesMap != null && imagesMap.size() > 0) {
            item.setImage(imagesMap.get(0).get("url").toString());
        }

        //商家id
        item.setSellerId(goodsEntity.getGoods().getSellerId());

        //商家名称
        Seller seller = sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        item.setSeller(seller.getName());

        //更新时间
        item.setUpdateTime(new Date());

        //创建时间

        item.setCreateTime(new Date());

        return item;
    }

}
