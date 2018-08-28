package cn.hm.core.service;

import java.util.Map;

public interface ItemSearchService {

    Map<String, Object> search(Map paramMap);

    Map<String, Object> highLightsEarch(Map paramMap);

}
