package cn.hm.core.utils;

import cn.hm.core.dao.item.ItemDao;
import cn.hm.core.pojo.item.Item;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemDao itemDao;

    public void importDBDataToSolr() {
        List<Item> items = itemDao.selectByExample(null);
        if (items != null) {
            for (Item item : items) {
                Map map = JSON.parseObject(item.getSpec(), Map.class);
                item.setSpecMap(map);
            }
        }
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) classPathXmlApplicationContext.getBean("solrUtil");
        solrUtil.importDBDataToSolr();
    }


}
