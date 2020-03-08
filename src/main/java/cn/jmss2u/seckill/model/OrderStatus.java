package cn.jmss2u.seckill.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/8 10:24
 */

public class OrderStatus implements Serializable {

    private Long userId;

    private Date createTime;

    //1 排队 2秒杀未支付 3支付超时 4秒杀失败  5.支付成功
    private Integer status;

    private Long goodsId;

    private BigDecimal money;

    private Long orderId;

    private String time;

    public OrderStatus() {

    }

    public OrderStatus(Long userId, Date createTime, Integer status, Long goodsId, String time) {
        this.userId=userId;
        this.createTime = createTime;
        this.status = status;
        this.goodsId = goodsId;
        this.time = time;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", status=").append(status);
        sb.append(", goodsId=").append(goodsId);
        sb.append(", money=").append(money);
        sb.append(", orderId=").append(orderId);
        sb.append(", time=").append(time);
        sb.append("]");
        return sb.toString();
    }

}
