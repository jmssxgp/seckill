package cn.jmss2u.seckill.dao;

import cn.jmss2u.seckill.model.Order;
import cn.jmss2u.seckill.model.OrderExample;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * OrderDAO继承基类
 */
@Repository
@Mapper
public interface OrderDAO extends MyBatisBaseDao<Order, Long, OrderExample> {
}