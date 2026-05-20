package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.entity.Activity;

/**
 * 价格计算器 —— 从 OrderController 抽离，可独立测试。
 * 支持三种活动类型：
 *   1. 折扣活动（discount）：newPrice = unitPrice × quantity × discount
 *   2. 满件减（fullnum / reducenum）：满 fullnum 件，减 reducenum 件的钱
 *   3. 满额减（fullprice / reduceprice）：满 fullprice 元，减 reduceprice 元
 */
public final class PriceCalculator {

    private PriceCalculator() {
    }

    /**
     * 根据活动计算单个商品的新价格（多件合计）。
     *
     * @param unitPrice 商品单价
     * @param quantity  购买数量
     * @param activity  绑定的活动（可为 null）
     * @return 计算后的新价格
     */
    public static float calculate(int unitPrice, int quantity, Activity activity) {
        if (quantity <= 0 || unitPrice <= 0) {
            return 0f;
        }

        float originalTotal = (float) unitPrice * quantity;

        // 无活动
        if (activity == null) {
            return originalTotal;
        }

        // 1. 折扣活动：discount 有意义且不为 1.0
        if (isDiscountActivity(activity)) {
            return originalTotal * activity.getDiscount();
        }

        // 2. 满件减：满 fullnum 件减 reducenum 件的价格
        if (isFullNumActivity(activity)) {
            if (quantity >= activity.getFullnum()) {
                int payableQty = quantity - activity.getReducenum();
                // 防呆：支付数量不能为负数
                if (payableQty < 0) {
                    payableQty = 0;
                }
                return (float) (unitPrice * payableQty);
            }
            return originalTotal;
        }

        // 3. 满额减：满 fullprice 元减 reduceprice 元
        if (isFullPriceActivity(activity)) {
            if (originalTotal >= activity.getFullprice()) {
                float reduced = originalTotal - activity.getReduceprice();
                // 防呆：实付不能为负数
                if (reduced < 0) {
                    return 0f;
                }
                return reduced;
            }
            return originalTotal;
        }

        // 无匹配的活动类型，按原价
        return originalTotal;
    }

    // ---- 活动类型判断 ----

    static boolean isDiscountActivity(Activity activity) {
        Float discount = activity.getDiscount();
        return discount != null && discount != 1.0f;
    }

    static boolean isFullNumActivity(Activity activity) {
        return activity.getFullnum() != null && activity.getReducenum() != null;
    }

    static boolean isFullPriceActivity(Activity activity) {
        return activity.getFullprice() != null && activity.getReduceprice() != null;
    }
}