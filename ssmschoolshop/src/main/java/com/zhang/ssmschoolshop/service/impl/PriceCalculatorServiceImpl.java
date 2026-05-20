package com.zhang.ssmschoolshop.service.impl;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Goods;
import com.zhang.ssmschoolshop.service.PriceCalculatorService;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculatorServiceImpl implements PriceCalculatorService {

    @Override
    public Float calculateNewPrice(Goods goods, Activity activity) {
        if (goods == null || goods.getPrice() == null || goods.getNum() == null) {
            return 0.0f;
        }

        Float originalPrice = (float) (goods.getPrice() * goods.getNum());

        if (activity == null) {
            return originalPrice;
        }

        Float priceWithDiscount = applyDiscount(originalPrice, activity);
        Float priceAfterFullNum = applyFullNum(priceWithDiscount, goods, activity);
        Float finalPrice = applyFullPrice(priceAfterFullNum, goods, activity);

        return finalPrice;
    }

    private Float applyDiscount(Float currentPrice, Activity activity) {
        if (activity.getDiscount() != null && activity.getDiscount() < 1) {
            return currentPrice * activity.getDiscount();
        }
        return currentPrice;
    }

    private Float applyFullNum(Float currentPrice, Goods goods, Activity activity) {
        if (activity.getFullnum() != null && activity.getReducenum() != null) {
            if (goods.getNum() >= activity.getFullnum()) {
                Float reduction = (float) (goods.getPrice() * activity.getReducenum());
                return Math.max(0, currentPrice - reduction);
            }
        }
        return currentPrice;
    }

    private Float applyFullPrice(Float currentPrice, Goods goods, Activity activity) {
        if (activity.getFullprice() != null && activity.getReduceprice() != null) {
            Float originalTotal = (float) (goods.getPrice() * goods.getNum());
            if (originalTotal >= activity.getFullprice()) {
                return Math.max(0, currentPrice - activity.getReduceprice());
            }
        }
        return currentPrice;
    }

    @Override
    public Float calculateTotalPrice(Goods goods) {
        if (goods == null || goods.getPrice() == null || goods.getNum() == null) {
            return 0.0f;
        }
        return (float) (goods.getPrice() * goods.getNum());
    }
}
