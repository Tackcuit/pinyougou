package cn.hm.core.service.impl;

import cn.hm.core.dao.good.GoodsDao;
import cn.hm.core.dao.good.GoodsDescDao;
import cn.hm.core.dao.item.ItemCatDao;
import cn.hm.core.dao.item.ItemDao;
import cn.hm.core.pojo.good.Goods;
import cn.hm.core.pojo.good.GoodsDesc;
import cn.hm.core.pojo.good.GoodsDescQuery;
import cn.hm.core.pojo.item.Item;
import cn.hm.core.pojo.item.ItemCat;
import cn.hm.core.pojo.item.ItemQuery;
import cn.hm.core.service.CmsService;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemCatDao itemCatDao;


    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * 拼接上项目地址
     * @param goodsid
     * @return
     */
    @Override
    public String getPath(Long goodsid) {
        return servletContext.getRealPath(getUrl(goodsid));
    }

    /**
     * 一个生成地址的方法
     * @param goodsid
     * @return
     */
    @Override
    public String getUrl(Long goodsid) {
        long l = goodsid / 30000;
        String path = "html/" + Long.toHexString(l) + "/" + goodsid + ".html";
        return path;
    }

    @Override
    public int createPage(Long[] ids) throws Exception {
        int temp = 0;
        if (ids != null) {
            for (Long id : ids) {
                Map<String, Object> paramMap = getParamMap(id);
                createStaticPage(id, paramMap);
                temp++;
            }
        }
        return temp;
    }

    public boolean createPath(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            createPath(file.getParent());
            return file.getParentFile().mkdir();
        }
        return true;
    }

    @Override
    public boolean createStaticPage(Long goodsid, Map<String, Object> paramMap) throws Exception {

        Configuration configuration = freeMarkerConfig.getConfiguration();
        Template template = configuration.getTemplate("item.ftl");
        String path = getPath(goodsid);     //获取地址
        if (!createPath(path)) {            //生成地址 然后判断成功
            throw new Exception("路径生成错误:商品ID:" + goodsid + "\n路径:" + path + "生成失败.");
        }
        Writer writer = new FileWriterWithEncoding(new File(path), "utf-8");

        template.process(paramMap, writer);
        return true;
    }

    @Override
    public Map<String, Object> getParamMap(Long goodsid) {

        Map<String, Object> paraMap = new HashMap<>();

        Goods goods = goodsDao.selectByPrimaryKey(goodsid);
        paraMap.put("goods", goods);

        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsid);
        paraMap.put("goodsDesc", goodsDesc);

        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(goodsid);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        paraMap.put("itemList", itemList);

        if (goods != null) {
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
            paraMap.put("itemCat1", itemCat1);
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
            paraMap.put("itemCat2", itemCat2);
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
            paraMap.put("itemCat3", itemCat3);
        }

        return paraMap;
    }


}
