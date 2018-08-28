package cn.hm.core.service.impl;

import cn.hm.core.dao.log.PayLogDao;
import cn.hm.core.dao.order.OrderDao;
import cn.hm.core.dao.order.OrderItemDao;
import cn.hm.core.pojo.entity.Cart;
import cn.hm.core.pojo.log.PayLog;
import cn.hm.core.pojo.order.Order;
import cn.hm.core.pojo.order.OrderItem;
import cn.hm.core.service.OrderService;
import cn.hm.core.utils.Constants;
import cn.hm.core.utils.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PayLogDao payLogDao;

    /**
     * 增加
     */
    @Override
    public void add(Order order) {
        //得到购物车数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(Constants.CART_REDIS_KEY).get(order.getUserId());

        List<String> orderIdList = new ArrayList();//订单ID列表
        double total_money = 0;//总金额 （元）

        for (Cart cart : cartList) {
            long orderId = idWorker.nextId();
            Order newOrder = new Order();//新创建订单对象
            newOrder.setOrderId(orderId);//订单ID
            newOrder.setUserId(order.getUserId());//用户名
            newOrder.setPaymentType(order.getPaymentType());//支付类型
            newOrder.setStatus("1");//状态：未付款
            newOrder.setCreateTime(new Date());//订单创建日期
            newOrder.setUpdateTime(new Date());//订单更新日期
            newOrder.setReceiverAreaName(order.getReceiverAreaName());//地址
            newOrder.setReceiverMobile(order.getReceiverMobile());//手机号
            newOrder.setReceiver(order.getReceiver());//收货人
            newOrder.setSourceType(order.getSourceType());//订单来源
            newOrder.setSellerId(cart.getSellerId());//商家ID
            //循环购物车明细
            double money = 0;
            for (OrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);//订单ID
                orderItem.setSellerId(cart.getSellerId());
                money += orderItem.getTotalFee().doubleValue();//金额累加
                orderItemDao.insertSelective(orderItem);
            }
            newOrder.setPayment(new BigDecimal(money));
            orderDao.insertSelective(newOrder);
            orderIdList.add(orderId + "");//添加到订单列表
            total_money += money;//累加到总金额
        }
        if ("1".equals(order.getPaymentType())) {//如果是微信支付
            PayLog payLog = new PayLog();
            String outTradeNo = idWorker.nextId() + "";//支付订单号
            payLog.setOutTradeNo(outTradeNo);//支付订单号
            payLog.setCreateTime(new Date());//创建时间
            //订单号列表，逗号分隔
            String ids = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(ids);//订单号列表，逗号分隔
            payLog.setPayType("1");//支付类型
            payLog.setTotalFee((long) (total_money * 100));//总金额(分)
            payLog.setTradeState("0");//支付状态
            payLog.setUserId(order.getUserId());//用户ID
            payLogDao.insert(payLog);//插入到支付日志表
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存
        }
        redisTemplate.boundHashOps(Constants.CART_REDIS_KEY).delete(order.getUserId());
    }


    @Override
    public PayLog searchPayLogFromRedis(String userId) {
        return (PayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //1.修改支付日志状态
        PayLog payLog = payLogDao.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());
        payLog.setTradeState("1");//已支付
        payLog.setTransactionId(transaction_id);//交易号
        payLogDao.updateByPrimaryKey(payLog);
        //2.修改订单状态
        String orderList = payLog.getOrderList();//获取订单号列表
        String[] orderIds = orderList.split(",");//获取订单号数组

        for (String orderId : orderIds) {
            Order order = orderDao.selectByPrimaryKey(Long.parseLong(orderId));
            if (order != null) {
                order.setStatus("2");//已付款
                orderDao.updateByPrimaryKey(order);
            }
        }
        //清除redis缓存数据
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }


}
