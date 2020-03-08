package cn.jmss2u.seckill.service.serviceImpl;

import cn.jmss2u.seckill.dao.GoodsDAO;
import cn.jmss2u.seckill.dao.OrderDAO;
import cn.jmss2u.seckill.model.Goods;
import cn.jmss2u.seckill.model.Order;
import cn.jmss2u.seckill.model.OrderStatus;
import cn.jmss2u.seckill.service.OrderService;
import cn.jmss2u.seckill.task.MultiThreadCreateOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/7 20:09
 */
@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    GoodsDAO goodsDAO;

    @Autowired
    MultiThreadCreateOrder multiThreadCreateOrder;

    @Autowired
    OrderDAO orderDAO;

    /**
     *
     * 这里是由前端传来的时区，可能有错，或者伪造的请求，
     * 需要对当前时间和其请求的商品的开始秒杀时间进行校验
     * 没写 = =
     *
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addOrder(Long id, String time, Long userId) {

        // 避免重复排队，根据商品id设置key，同一商品下，同一个用户只允许排一次队，每次点击抢购时，数量+1
        // 若结果大于1，则存在重复排队现象，直接返回，不创建订单。
        Long userQueueCount = redisTemplate.boundHashOps("userQueueCount"+id).increment(userId, 1);
        if (userQueueCount!=1){
            return false;
        }

        Long size = redisTemplate.boundListOps("goodsCount"+id).size();
        if (size == 0){
            // 商品售空，无需排队
            return false;
        }

        // 创建订单状态，将排队信息入队列
        OrderStatus orderStatus = new OrderStatus(userId, new Date(), 1, id, time);

        redisTemplate.boundListOps("OrderQueue").leftPush(orderStatus);

        // 将用户排队信息存入redis，以备前端查看用户订单状态，key为用户id
        redisTemplate.boundHashOps("UserOrderStatus").put(userId, orderStatus);

        multiThreadCreateOrder.createOrder();

        return true;
    }

    //用户当前订单状态查询
    @Override
    @SuppressWarnings("unchecked")
    public OrderStatus queryStatus(Long userId) {
        OrderStatus orderStatus = (OrderStatus) redisTemplate.boundHashOps("UserOrderStatus").get(userId);
        if (orderStatus==null) {
            orderStatus = new OrderStatus(userId, new Date(), 4, 0L,"0");
        }
        return orderStatus;
    }

    /**
     * 此处为更新订单状态，例如支付完成后调用，更新状态为已支付
     * 支付没法写，这里只是单纯写一下
     * @param userId
     * @param goodsId
     */
    @Override
    @SuppressWarnings("unchecked")
    public void updateStatus(Long userId, Long goodsId) {
        Order order = (Order) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (order != null){
            order.setCreateTime(new Date());
            order.setStatus("1");
            orderDAO.insertSelective(order);

            redisTemplate.boundHashOps("seckillOrder").delete(userId);

            redisTemplate.boundHashOps("userQueueCount"+goodsId).delete(userId);

            redisTemplate.boundHashOps("UserOrderStatus").delete(userId);
        }


    }
}
