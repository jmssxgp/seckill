package cn.jmss2u.seckill.controller;

import cn.jmss2u.seckill.model.Goods;
import cn.jmss2u.seckill.service.serviceImpl.GoodsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/7 10:12
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsServiceImpl goodsService;

    @PostMapping("/addGoodsSelective")
    public String addGoodsSelective(@RequestBody Goods goods){
        return goodsService.addGoods(goods);
    }


    @GetMapping("/selectById")
    public Goods selectById(@RequestParam String time, @RequestParam Long id){
        return goodsService.selectById(time, id);
    }

    /**
     * 根据时间获取秒杀商品列表
     * @param time
     * @return
     */
    @GetMapping("/getList")
    public List<Goods> selectByTime(@RequestParam String time){
        return goodsService.selectByTime(time);
    }

}
