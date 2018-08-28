package cn.hm.core.service;

public interface ItemManagerService {

    int itemToSolr(Long[] ids);

    int deleteItemFromSolr(Long[] ids);

}
