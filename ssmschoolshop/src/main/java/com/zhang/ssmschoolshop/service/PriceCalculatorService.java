package com.zhang.ssmschoolshop.service;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Goods;

public interface PriceCalculatorService {
    Float calculateNewPrice(Goods goods, Activity activity);
    Float calculateTotalPrice(Goods goods);
}
