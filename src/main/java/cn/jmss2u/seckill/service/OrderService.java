package cn.jmss2u.seckill.service;

import cn.jmss2u.seckill.model.OrderStatus;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/7 20:09
 */
public interface OrderService {


    boolean addOrder(Long id, String time, Long userId);

    OrderStatus queryStatus(Long userId);

    void updateStatus(Long userId, Long goodsId);
}
