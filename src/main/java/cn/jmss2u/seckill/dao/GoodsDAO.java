package cn.jmss2u.seckill.dao;

import cn.jmss2u.seckill.model.Goods;
import cn.jmss2u.seckill.model.GoodsExample;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import javax.annotation.ManagedBean;

/**
 * GoodsDAO继承基类
 */
@Repository
@Mapper
public interface GoodsDAO extends MyBatisBaseDao<Goods, Long, GoodsExample> {
}