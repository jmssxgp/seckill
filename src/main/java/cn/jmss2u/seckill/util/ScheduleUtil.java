package cn.jmss2u.seckill.util;

import cn.jmss2u.seckill.dao.GoodsDAO;
import cn.jmss2u.seckill.model.Goods;
import cn.jmss2u.seckill.model.GoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.SimpleFormatter;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/7 15:06
 */
@Component
@EnableScheduling
public class ScheduleUtil {


    @Autowired
    GoodsDAO goodsDAO;

    @Autowired
    RedisTemplate redisTemplate;

    /***
     * 定时任务，每隔60s时间从数据库中读取五个时段的
     * 秒杀商品到redis缓存
     */

    @Scheduled(cron = "0/10 * * * * ?")
    @SuppressWarnings("unchecked")
    public void loadGoods(){
        List<Date> menu = TimeUtil.getDateMenu();
        /**
         * 商品审核状态为1，库存大于0，大于等于起始时间，小于结束时间
         */
        for (Date date:menu){
            GoodsExample example = new GoodsExample();
            GoodsExample.Criteria criteria = example.createCriteria();
            Date endTime = TimeUtil.addDateHour(date, 2);
            criteria.andStatusEqualTo("1")
                    .andStockCountGreaterThan(0)
                    .andStartTimeGreaterThanOrEqualTo(date)
                    .andStartTimeLessThan(endTime);

            // 筛选掉redis已有数据
            Set keys = redisTemplate.boundHashOps("SeckillGoods_"+TimeUtil.date2Str(date)).keys();
            List key = new ArrayList();
            if (keys!=null){
                key.addAll(keys);
            }
            if (key.size()>0){
                criteria.andIdNotIn(key);
            }


            List<Goods> goods = goodsDAO.selectByExample(example);
            // 添加到redis缓存中
            for (Goods g : goods
                    ) {
                // 按时间段分组存储商品
                redisTemplate.boundHashOps("SeckillGoods_"+TimeUtil.date2Str(date)).put(g.getId(), g);
                // 避免超卖，在队列中按商品个数放置元素
                redisTemplate.boundListOps("goodsCount"+g.getId()).leftPushAll(pushIds(g.getStockCount(), g.getId()));
                // 避免数据不精准，并发操作导致商品为0时，redis中的商品数据中的stoke仍不为0
                // 虽然不会引起超卖等现象，但是无法更新数据库，数据不精准，使用redis记录商品数量
                Long t = redisTemplate.boundHashOps("goodsStoke").increment(g.getId(), g.getStockCount());

            }
        }


    }


    public Long[] pushIds(int len, Long goodsId){
        Long[] ids = new Long[len];
        Arrays.fill(ids, goodsId);
        return ids;
    }
}
