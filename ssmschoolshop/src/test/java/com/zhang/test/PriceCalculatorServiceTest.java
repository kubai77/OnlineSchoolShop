package com.zhang.test;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Goods;
import com.zhang.ssmschoolshop.service.impl.PriceCalculatorServiceImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class PriceCalculatorServiceTest {

    private PriceCalculatorServiceImpl priceCalculatorService = new PriceCalculatorServiceImpl();

    @Test
    public void testNoActivity() {
        Goods goods = new Goods();
        goods.setPrice(100);
        goods.setNum(2);

        Float newPrice = priceCalculatorService.calculateNewPrice(goods, null);

        assertEquals(Float.valueOf(200.0f), newPrice);
    }

    @Test
    public void testDiscountActivity() {
        Goods goods = new Goods();
        goods.setPrice(100);
        goods.setNum(2);

        Activity activity = new Activity();
        activity.setDiscount(0.8f);

        Float newPrice = priceCalculatorService.calculateNewPrice(goods, activity);

        assertEquals(Float.valueOf(160.0f), newPrice);
    }

    @Test
    public void testFullNumReduce() {
        Goods goods = new Goods();
        goods.setPrice(100);
        goods.setNum(3);

        Activity activity = new Activity();
        activity.setFullnum(3);
        activity.setReducenum(1);

        Float newPrice = priceCalculatorService.calculateNewPrice(goods, activity);

        assertEquals(Float.valueOf(200.0f), newPrice);
    }

    @Test
    public void testFullNumNotReach() {
        Goods goods = new Goods();
        goods.setPrice(100);
        goods.setNum(2);

        Activity activity = new Activity();
        activity.setFullnum(3);
        activity.setReducenum(1);

        Float newPrice = priceCalculatorService.calculateNewPrice(goods, activity);

        assertEquals(Float.valueOf(200.0f), newPrice);
    }

    @Test
    public void testFullPriceReduce() {
        Goods goods = new Goods();
        goods.setPrice(100);
        goods.setNum(3);

        Activity activity = new Activity();
        activity.setFullprice(250);
        activity.setReduceprice(50);

        Float newPrice = priceCalculatorService.calculateNewPrice(goods, activity);

        assertEquals(Float.valueOf(250.0f), newPrice);
    }

    @Test
    public void testFullPriceNotReach() {
        Goods goods = new Goods();
        goods.setPrice(100);
        goods.setNum(2);

        Activity activity = new Activity();
        activity.setFullprice(250);
        activity.setReduceprice(50);

        Float newPrice = priceCalculatorService.calculateNewPrice(goods, activity);

        assertEquals(Float.valueOf(200.0f), newPrice);
    }

    @Test
    public void testMultipleActivities() {
        Goods goods = new Goods();
        goods.setPrice(100);
        goods.setNum(3);

        Activity activity = new Activity();
        activity.setDiscount(0.9f);
        activity.setFullnum(3);
        activity.setReducenum(1);
        activity.setFullprice(200);
        activity.setReduceprice(30);

        Float newPrice = priceCalculatorService.calculateNewPrice(goods, activity);

        assertEquals(Float.valueOf(140.0f), newPrice);
    }

    @Test
    public void testBoundaryValue() {
        Goods goods = new Goods();
        goods.setPrice(100);
        goods.setNum(3);

        Activity activity = new Activity();
        activity.setFullprice(300);
        activity.setReduceprice(300);

        Float newPrice = priceCalculatorService.calculateNewPrice(goods, activity);

        assertEquals(Float.valueOf(0.0f), newPrice);
    }

    @Test
    public void testNullValues() {
        Float price1 = priceCalculatorService.calculateNewPrice(null, null);
        assertEquals(Float.valueOf(0.0f), price1);

        Goods goods = new Goods();
        Float price2 = priceCalculatorService.calculateNewPrice(goods, null);
        assertEquals(Float.valueOf(0.0f), price2);
    }

    @Test
    public void testCalculateTotalPrice() {
        Goods goods = new Goods();
        goods.setPrice(100);
        goods.setNum(5);

        Float totalPrice = priceCalculatorService.calculateTotalPrice(goods);

        assertEquals(Float.valueOf(500.0f), totalPrice);
    }
}
