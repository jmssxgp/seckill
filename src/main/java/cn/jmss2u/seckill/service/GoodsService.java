package cn.jmss2u.seckill.service;

import cn.jmss2u.seckill.model.Goods;

import java.util.Date;
import java.util.List;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/7 9:57
 */
public interface GoodsService {

    String addGoods(Goods goods);

    Goods selectById(String time, Long id);

    List<Goods> selectByTime(String time);
}
