package cn.jmss2u.seckill.task;

import cn.jmss2u.seckill.config.DelayQueueConfig;
import cn.jmss2u.seckill.dao.GoodsDAO;
import cn.jmss2u.seckill.model.Goods;
import cn.jmss2u.seckill.model.Order;
import cn.jmss2u.seckill.model.OrderStatus;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/8 17:05
 */
@Service
@EnableRabbit
public class DelayQueueReceiver {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    GoodsDAO goodsDAO;

    // 监听死信
    @RabbitListener(queues = DelayQueueConfig.DELAY_PROCESS_QUEUE_NAME)
    @RabbitHandler
    public void process(Message message){
        String content = new String(message.getBody());
        System.out.println(content);
        OrderStatus orderStatus = JSON.parseObject(content, OrderStatus.class);
        System.out.println(orderStatus);
        rollBackOrder(orderStatus);
    }


    /**
     * 订单回滚
     * @param orderStatus
     */
    @SuppressWarnings("unchecked")
    public void rollBackOrder(OrderStatus orderStatus) {

        if (orderStatus == null) return;
        // 判断redis中是否有相应订单，删除超时订单

        Order order = (Order) redisTemplate.boundHashOps("seckillOrder").get(orderStatus.getUserId());

        // redis中仍有该订单，显然未支付
        if (order != null) {
            // 删除用户订单
            redisTemplate.boundHashOps("seckillOrder").delete(orderStatus.getUserId());

            Goods goods = (Goods) redisTemplate.boundHashOps("SeckillGoods_" + orderStatus.getTime()).get(orderStatus.getGoodsId());

            // 已抢空，redis被清除
            if (goods == null) {
                goods = goodsDAO.selectByPrimaryKey(orderStatus.getGoodsId());
            }

            //增加库存
            Long stoke = redisTemplate.boundHashOps("goodsStoke").increment(goods.getId(), 1);
            goods.setStockCount(stoke.intValue());

            // 更新redis

            redisTemplate.boundHashOps("SeckillGoods_" + orderStatus.getTime()).put(goods.getId(), goods);
            redisTemplate.boundListOps("goodsCount" + goods.getId()).leftPush(goods.getId());

            // 清除用户订单状态信息
            redisTemplate.boundHashOps("UserOrderStatus").delete(orderStatus.getUserId());
            // 清除重复排队标识信息
            redisTemplate.boundHashOps("userQueueCount"+goods.getId()).delete(orderStatus.getUserId());
        }
    }
}
