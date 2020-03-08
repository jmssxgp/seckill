package cn.jmss2u.seckill.service.serviceImpl;

import cn.jmss2u.seckill.dao.GoodsDAO;
import cn.jmss2u.seckill.model.Goods;
import cn.jmss2u.seckill.model.GoodsExample;
import cn.jmss2u.seckill.service.GoodsService;
import cn.jmss2u.seckill.util.ScheduleUtil;
import cn.jmss2u.seckill.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/7 9:58
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsDAO goodsDao;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ScheduleUtil scheduleUtil;

    @Override
    public String addGoods(Goods goods) {
        int res = goodsDao.insert(goods);
        System.out.println(res);
        if (res>0){
            return "插入成功";
        }else {
            return "插入失败";
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Goods selectById(String time, Long id) {
        return (Goods)redisTemplate.boundHashOps("SeckillGoods_"+time).get(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Goods> selectByTime(String time) {

        String key = "SeckillGoods_" + time;
        List res = redisTemplate.boundHashOps(key).values();

        // 若缓存没有，则从数据库中获取，并添加到redis缓存中
        if (res == null){
            scheduleUtil.loadGoods();
        }
        res = redisTemplate.boundHashOps(key).values();
        return res;
    }
}
