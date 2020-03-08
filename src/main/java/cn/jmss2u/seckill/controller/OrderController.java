package cn.jmss2u.seckill.controller;

import cn.jmss2u.seckill.model.Goods;
import cn.jmss2u.seckill.model.OrderStatus;
import cn.jmss2u.seckill.service.serviceImpl.OrderServiceImpl;
import cn.jmss2u.seckill.task.MultiThreadCreateOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/7 20:26
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderServiceImpl orderService;


    @PostMapping("/addOrder")
    @SuppressWarnings("unchecked")
    public boolean addOrder(Long id, String time, Long userId){
        return orderService.addOrder(id, time, userId);
    }

    @GetMapping("/OrderStatus")
    public OrderStatus queryStatus(Long userId){
        return orderService.queryStatus(userId);
    }

    @PostMapping("/pay")
    public boolean payForOrder(){
        /**
         * 支付订单，删除redis中的订单信息，但不删除重复排队标识，避免多次购买，持久化订单
         */

        return true;
    }

}
