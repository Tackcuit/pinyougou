package cn.hm.core.controller;

import cn.hm.core.service.CmsService;
import cn.hm.core.service.ItemManagerService;
import cn.hm.core.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    @Reference
    private CmsService cmsService;

    @Value("${SERVICE_PAGE_URL}")
    private String SERVICE_PAGE_URL;

    @RequestMapping("/search")
    public Map<String, Object> search(@RequestBody Map paramMap) {
        return itemSearchService.highLightsEarch(paramMap);
    }

    @RequestMapping("/getUrl")
    public Map<String, String> getUrl(Long id) {
        String path = cmsService.getUrl(id);
        Map<String, String> map = new HashMap<>();
        map.put("url", SERVICE_PAGE_URL + path);
        return map;
    }
}
