package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Goods;

public class PriceCalculator {

    /**
     * 计算商品在活动下的最新价格
     * 支持：折扣、满件减、满额减
     * @param goods 商品信息，包含数量和原单价
     * @param activity 活动信息，可能为空
     * @return 最终价格
     */
    public static float calculateNewPrice(Goods goods, Activity activity) {
        float originalPrice = goods.getPrice() != null ? goods.getPrice() : 0f;
        int num = goods.getNum() != null ? goods.getNum() : 0;
        float totalOriginalPrice = originalPrice * num;

        if (activity == null) {
            return totalOriginalPrice;
        }

        // 1. 折扣活动
        if (activity.getDiscount() != null && Math.abs(activity.getDiscount() - 1.0f) > 0.0001) {
            return totalOriginalPrice * activity.getDiscount();
        }

        // 2. 满件减（买满 fullnum 件，减免 reducenum 件的金额）
        if (activity.getFullnum() != null && activity.getReducenum() != null) {
            if (num >= activity.getFullnum()) {
                int payableNum = Math.max(0, num - activity.getReducenum());
                return originalPrice * payableNum;
            }
        }

        // 3. 满额减（买满 fullprice 减 reduceprice）
        if (activity.getFullprice() != null && activity.getReduceprice() != null) {
            if (totalOriginalPrice >= activity.getFullprice()) {
                return Math.max(0, totalOriginalPrice - activity.getReduceprice());
            }
        }

        return totalOriginalPrice;
    }
}
