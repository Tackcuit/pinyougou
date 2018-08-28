package cn.hm.core.service;

import cn.hm.core.pojo.log.PayLog;
import cn.hm.core.pojo.order.Order;

public interface OrderService {

    public void add(Order order);

    public PayLog searchPayLogFromRedis(String userId);

    public void updateOrderStatus(String out_trade_no,String transaction_id);

}
