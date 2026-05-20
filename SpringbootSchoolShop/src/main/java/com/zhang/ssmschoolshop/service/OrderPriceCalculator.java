package com.zhang.ssmschoolshop.service;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Goods;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderPriceCalculator {

    public float calculateOriginalPrice(Goods goods) {
        if (goods == null || goods.getPrice() == null || goods.getNum() == null) {
            return 0F;
        }
        return goods.getPrice() * goods.getNum();
    }

    public float calculateNewPrice(Goods goods) {
        Activity activity = goods == null ? null : goods.getActivity();
        return calculateNewPrice(goods, activity);
    }

    public float calculateNewPrice(Goods goods, Activity activity) {
        float originalPrice = calculateOriginalPrice(goods);
        if (goods == null || activity == null) {
            return originalPrice;
        }

        List<Float> candidatePrices = new ArrayList<>();
        candidatePrices.add(originalPrice);

        if (hasDiscount(activity)) {
            candidatePrices.add(originalPrice * activity.getDiscount());
        }

        if (hasFullNumReduction(activity) && goods.getNum() >= activity.getFullnum()) {
            int payableNum = Math.max(0, goods.getNum() - activity.getReducenum());
            candidatePrices.add(goods.getPrice() * payableNum * 1F);
        }

        if (hasFullPriceReduction(activity) && originalPrice >= activity.getFullprice()) {
            candidatePrices.add(Math.max(0F, originalPrice - activity.getReduceprice()));
        }

        float bestPrice = candidatePrices.get(0);
        for (Float candidatePrice : candidatePrices) {
            if (candidatePrice < bestPrice) {
                bestPrice = candidatePrice;
            }
        }
        return bestPrice;
    }

    public void applyNewPrice(Goods goods) {
        if (goods == null) {
            return;
        }
        goods.setNewPrice(calculateNewPrice(goods));
    }

    private boolean hasDiscount(Activity activity) {
        return activity.getDiscount() != null
                && activity.getDiscount() > 0F
                && activity.getDiscount() < 1F;
    }

    private boolean hasFullPriceReduction(Activity activity) {
        return activity.getFullprice() != null
                && activity.getFullprice() > 0
                && activity.getReduceprice() != null
                && activity.getReduceprice() > 0;
    }

    private boolean hasFullNumReduction(Activity activity) {
        return activity.getFullnum() != null
                && activity.getFullnum() > 0
                && activity.getReducenum() != null
                && activity.getReducenum() > 0;
    }
}
