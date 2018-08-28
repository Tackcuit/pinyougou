package cn.hm.core.service;

import cn.hm.core.pojo.entity.Cart;

import java.util.List;

public interface CartService {


    public List<Cart> getCartListFromRedis(String userName);

    public List<Cart> copyAllCart(List<Cart> list1, List<Cart> list2);


    List<Cart> addCart(List<Cart> cartList, Long itemId, Integer num);

    void saveCartListToRedis(String userName,List<Cart> CartList);
}
