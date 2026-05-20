package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Goods;

/**
 * 价格计算器 - 独立于控制器的优惠计算模块
 * 支持三类优惠活动：折扣、满件减、满额减
 */
public class PriceCalculator {

    /**
     * 计算商品最终价格
     * @param goods 商品信息（包含数量和单价）
     * @param activity 活动信息，null表示无活动
     * @return 计算后的新价格
     */
    public static float calculate(Goods goods, Activity activity) {
        if (goods == null || goods.getPrice() == null || goods.getNum() == null) {
            return 0f;
        }

        float originalPrice = goods.getPrice() * goods.getNum();

        // 无活动或活动对象为空
        if (activity == null) {
            return originalPrice;
        }

        // 折扣活动 (discount != 1 表示有折扣)
        if (activity.getDiscount() != null && activity.getDiscount() != 1.0f) {
            return originalPrice * activity.getDiscount();
        }

        // 满件减活动
        if (activity.getFullnum() != null && activity.getFullnum() > 0) {
            if (goods.getNum() >= activity.getFullnum()) {
                // 满fullnum件，减reducenum件的钱（即减reducenum个商品的价格）
                int reduceCount = activity.getReducenum() != null ? activity.getReducenum() : 0;
                return originalPrice - (reduceCount * goods.getPrice());
            }
        }

        // 满额减活动
        if (activity.getFullprice() != null && activity.getFullprice() > 0) {
            if (originalPrice >= activity.getFullprice()) {
                // 满fullprice元，减reduceprice元
                int reduceAmount = activity.getReduceprice() != null ? activity.getReduceprice() : 0;
                return originalPrice - reduceAmount;
            }
        }

        // 无满足条件，返回原价
        return originalPrice;
    }

    /**
     * 计算单商品单价（用于展示）
     * @param unitPrice 商品单价
     * @param activity 活动信息
     * @param num 购买数量
     * @return 计算后的新单价
     */
    public static float calculateUnitPrice(int unitPrice, Activity activity, int num) {
        if (unitPrice <= 0 || num <= 0) {
            return 0f;
        }

        float originalPrice = unitPrice * num;

        // 无活动
        if (activity == null) {
            return originalPrice;
        }

        // 折扣活动
        if (activity.getDiscount() != null && activity.getDiscount() != 1.0f) {
            return originalPrice * activity.getDiscount();
        }

        // 满件减
        if (activity.getFullnum() != null && activity.getFullnum() > 0) {
            if (num >= activity.getFullnum()) {
                int reduceCount = activity.getReducenum() != null ? activity.getReducenum() : 0;
                return originalPrice - (reduceCount * unitPrice);
            }
        }

        // 满额减
        if (activity.getFullprice() != null && activity.getFullprice() > 0) {
            if (originalPrice >= activity.getFullprice()) {
                int reduceAmount = activity.getReduceprice() != null ? activity.getReduceprice() : 0;
                return originalPrice - reduceAmount;
            }
        }

        return originalPrice;
    }
}