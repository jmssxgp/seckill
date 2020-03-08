package cn.jmss2u.seckill.task;

import cn.jmss2u.seckill.config.DelayQueueConfig;
import cn.jmss2u.seckill.dao.GoodsDAO;
import cn.jmss2u.seckill.model.Goods;
import cn.jmss2u.seckill.model.Order;
import cn.jmss2u.seckill.model.OrderStatus;
import cn.jmss2u.seckill.service.serviceImpl.OrderServiceImpl;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/8 10:10
 */


@Component
public class MultiThreadCreateOrder {


    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    GoodsDAO goodsDAO;

    @Async("asyncServiceExecutor")
    @SuppressWarnings("unchecked")
    public void createOrder(){

        OrderStatus orderStatus = (OrderStatus) redisTemplate.boundListOps("OrderQueue").rightPop();
        String time = orderStatus.getTime();
        Long id = orderStatus.getGoodsId();
        Long userId = orderStatus.getUserId();

        // 获取队列中的商品，是否有库存
        Object ids = redisTemplate.boundListOps("goodsCount"+id).rightPop();
        if (ids == null){
            // 该商品售空，清理排队信息
            clearRedis(orderStatus);
            return;
        }
        Goods goods = (Goods) redisTemplate.boundHashOps("SeckillGoods_"+time).get(id);
        if (goods!=null&&goods.getStockCount()>0){
            Order order = new Order();
            order.setSeckillId(id);
            order.setMoney(goods.getCostPrice());
            order.setCreateTime(new Date());
            order.setSellerId(goods.getSellerId());
            order.setUserId(userId);
            order.setStatus("0");

            // 添加订单，状态为队列等待
            redisTemplate.boundHashOps("seckillOrder").put(userId, order);

            Long stoke = redisTemplate.boundHashOps("goodsStoke").increment(id, -1);

            goods.setStockCount(stoke.intValue());

            // 商品售空，写回mysql，清理缓存
            if (stoke<=0){
                goodsDAO.updateByPrimaryKey(goods);
                redisTemplate.boundHashOps("SeckillGoods_"+time).delete(id);
            }else {
                redisTemplate.boundHashOps("SeckillGoods_"+time).put(id, goods);
            }
        }

        try {
            Thread.sleep(3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        // 3s后变更排队状态,供前端查询
        orderStatus.setStatus(2);
        orderStatus.setMoney(goods.getCostPrice());
        redisTemplate.boundHashOps("UserOrderStatus").put(userId, orderStatus);
        System.out.println("调用延时队列");
        // 将状态发送到延时队列
        sendDelayMessage(orderStatus);
    }


    private void clearRedis(OrderStatus orderStatus){
        // 清除用户订单状态信息
        redisTemplate.boundHashOps("UserOrderStatus").delete(orderStatus.getUserId());
        // 清除重复排队标识信息
        redisTemplate.boundHashOps("userQueueCount"+orderStatus.getGoodsId()).delete(orderStatus.getUserId());
    }


    @Autowired
    RabbitTemplate rabbitTemplate;

    // 放入延时队列
    public void sendDelayMessage(OrderStatus orderStatus){
        rabbitTemplate.convertAndSend(
                DelayQueueConfig.DELAY_QUEUE_PER_QUEUE_TTL_NAME,
                JSON.toJSONString(orderStatus)
        );
    }
}
