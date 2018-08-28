package cn.hm.core.service.impl;

import cn.hm.core.dao.item.ItemDao;
import cn.hm.core.pojo.entity.Cart;
import cn.hm.core.pojo.item.Item;
import cn.hm.core.pojo.order.OrderItem;
import cn.hm.core.service.CartService;
import cn.hm.core.utils.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ItemDao itemDao;

    @Override
    public List<Cart> getCartListFromRedis(String userName) {
        List<Cart> carts = (List<Cart>) redisTemplate.boundHashOps(Constants.CART_REDIS_KEY).get(userName);
        return carts != null ? carts : new ArrayList<>();
    }

    @Override
    public List<Cart> copyAllCart(List<Cart> list1, List<Cart> list2) {
//        ArrayList<Cart> carts = new ArrayList<>();
//        for (Cart cart : list1) {
//            for (Cart cart1 : list2) {
//                if (cart.getSellerId().equals(cart1.getSellerId())) {
//                    //融合两个集合
//                    Collections.copy(cart.getOrderItemList(), cart1.getOrderItemList());
//                    //集合去重
//                    cart.getOrderItemList().stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(OrderItem::getId))), ArrayList::new));
//                    carts.add(cart);
//                } else {
//                    carts.add(cart1);
//                }
//            }
//        }
        for (Cart cookieCart : list2) {
            for (OrderItem cookieOrderItem : cookieCart.getOrderItemList()) {
                addCart(list1, cookieOrderItem.getItemId(), cookieOrderItem.getNum());
            }
        }
        return list1;
    }

    @Override
    public List<Cart> addCart(List<Cart> cartList, Long itemId, Integer num) {
        //获取商品信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //判断商品信息是否真实
        if (item == null)
            throw new RuntimeException("没有此商品");
        if (!"1".equals(item.getStatus()))
            throw new RuntimeException("商品没通过审核");
        //获取商品的卖家ID
        String sellerId = item.getSellerId();
        //判断有没有这家的购物车
        Cart nowCart = null;
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerId())) {
                nowCart = cart;
            }
        }
        //判断购物车有没有
        if (nowCart == null) {    //没有

            nowCart = new Cart();           //建新购物车

            nowCart.setSellerId(sellerId);      //设置商家ID

            nowCart.setSellerName(item.getSeller());        //设置商家名称

            ArrayList<OrderItem> orderItems = new ArrayList<>();        //购物列表

            orderItems.add(createOrderItem(item, num));         //添加购物列表信息

            nowCart.setOrderItemList(orderItems);           //设置购物列表

            cartList.add(nowCart);          //添加进购物车列表

        } else {                //有
            //获取购物列表
            List<OrderItem> orderItemList = nowCart.getOrderItemList();
            //建新商品明细
            OrderItem nowOrderItem = null;
            //查看有没有这个商品
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(item.getId())) {
                    nowOrderItem = orderItem;
                }
            }
            //判断有没有商品
            if (nowOrderItem == null) {     //不存在此商品
                //造一个商品详情添加进购物列表
                orderItemList.add(createOrderItem(item, num));
                //把购物列表添加进现在的购物车
                nowCart.setOrderItemList(orderItemList);
                //添加进购物车列表
                cartList.add(nowCart);
            } else {
                //存在这个商品
                //增加商品的数量
                nowOrderItem.setNum(nowOrderItem.getNum() + num);
                //改变总金额
                nowOrderItem.setTotalFee(new BigDecimal(nowOrderItem.getNum()).multiply(nowOrderItem.getPrice()));
                //把商品信息回写到购物车

                if (nowOrderItem.getNum() <= 0) {
                    nowCart.getOrderItemList().remove(nowOrderItem);
                }
                if (nowCart.getOrderItemList() == null || nowCart.getOrderItemList().size() == 0) {
                    cartList.remove(nowCart);
                }
            }
        }
        return cartList;
    }

    public OrderItem createOrderItem(Item item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("购买数量错误!");
        }
        OrderItem orderItem = new OrderItem();
        //商品id
        orderItem.setGoodsId(item.getGoodsId());
        //库存id
        orderItem.setItemId(item.getId());
        //购买数量
        orderItem.setNum(num);
        //图片路径
        orderItem.setPicPath(item.getImage());
        //单价
        orderItem.setPrice(item.getPrice());
        //卖家id
        orderItem.setSellerId(item.getSellerId());
        //商品名称
        orderItem.setTitle(item.getTitle());
        //总价
        orderItem.setTotalFee(new BigDecimal(num).multiply(item.getPrice()));
        return orderItem;
    }

    @Override
    public void saveCartListToRedis(String userName, List<Cart> CartList) {
        redisTemplate.boundHashOps(Constants.CART_REDIS_KEY).put(userName, CartList);
    }


}
