package cn.hm.core.service;

import java.util.Map;

public interface CmsService {

    boolean createStaticPage(Long goodsid, Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getParamMap(Long goodsid);

    String getPath(Long goodsid);

    int createPage(Long[] ids) throws Exception;

    String getUrl(Long goodsid);
}
