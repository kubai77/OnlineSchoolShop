package com.zhang.ssmschoolshop.service;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Goods;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderPriceCalculatorTest {

    private OrderPriceCalculator orderPriceCalculator;

    @Before
    public void setUp() {
        orderPriceCalculator = new OrderPriceCalculator();
    }

    @Test
    public void shouldKeepOriginalPriceWhenNoActivity() {
        Goods goods = buildGoods(100, 2);

        assertEquals(200F, orderPriceCalculator.calculateNewPrice(goods, null), 0.001F);
    }

    @Test
    public void shouldApplyDiscountActivity() {
        Goods goods = buildGoods(100, 2);
        Activity activity = new Activity();
        activity.setDiscount(0.8F);

        assertEquals(160F, orderPriceCalculator.calculateNewPrice(goods, activity), 0.001F);
    }

    @Test
    public void shouldApplyFullNumReductionActivity() {
        Goods goods = buildGoods(100, 4);
        Activity activity = new Activity();
        activity.setFullnum(3);
        activity.setReducenum(1);

        assertEquals(300F, orderPriceCalculator.calculateNewPrice(goods, activity), 0.001F);
    }

    @Test
    public void shouldApplyFullPriceReductionActivity() {
        Goods goods = buildGoods(100, 4);
        Activity activity = new Activity();
        activity.setFullprice(300);
        activity.setReduceprice(40);

        assertEquals(360F, orderPriceCalculator.calculateNewPrice(goods, activity), 0.001F);
    }

    @Test
    public void shouldRespectBoundaryValues() {
        Goods goodsForFullNum = buildGoods(100, 3);
        Activity fullNumActivity = new Activity();
        fullNumActivity.setFullnum(3);
        fullNumActivity.setReducenum(1);

        Goods goodsForFullPrice = buildGoods(100, 3);
        Activity fullPriceActivity = new Activity();
        fullPriceActivity.setFullprice(300);
        fullPriceActivity.setReduceprice(50);

        assertEquals(200F, orderPriceCalculator.calculateNewPrice(goodsForFullNum, fullNumActivity), 0.001F);
        assertEquals(250F, orderPriceCalculator.calculateNewPrice(goodsForFullPrice, fullPriceActivity), 0.001F);
    }

    @Test
    public void shouldChooseLowestPriceWhenMultipleRulesAreConfigured() {
        Goods goods = buildGoods(100, 4);
        Activity activity = new Activity();
        activity.setDiscount(0.9F);
        activity.setFullprice(300);
        activity.setReduceprice(30);
        activity.setFullnum(3);
        activity.setReducenum(1);

        assertEquals(300F, orderPriceCalculator.calculateNewPrice(goods, activity), 0.001F);
    }

    private Goods buildGoods(int price, int num) {
        Goods goods = new Goods();
        goods.setPrice(price);
        goods.setNum(num);
        return goods;
    }
}
