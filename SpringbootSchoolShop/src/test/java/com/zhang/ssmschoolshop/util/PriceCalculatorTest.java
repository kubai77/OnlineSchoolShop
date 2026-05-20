package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Goods;
import org.junit.Assert;
import org.junit.Test;

public class PriceCalculatorTest {

    @Test
    public void testNoActivity() {
        Goods goods = new Goods();
        goods.setPrice(100f);
        goods.setNum(2);

        float price = PriceCalculator.calculateNewPrice(goods, null);
        Assert.assertEquals(200f, price, 0.001);
    }

    @Test
    public void testDiscountActivity() {
        Goods goods = new Goods();
        goods.setPrice(100f);
        goods.setNum(2);

        Activity activity = new Activity();
        activity.setDiscount(0.8f);

        float price = PriceCalculator.calculateNewPrice(goods, activity);
        Assert.assertEquals(160f, price, 0.001); // 200 * 0.8
    }

    @Test
    public void testFullNumReductionActivity() {
        Goods goods = new Goods();
        goods.setPrice(100f);
        goods.setNum(3);

        Activity activity = new Activity();
        activity.setDiscount(1.0f); // 默认不打折
        activity.setFullnum(3);
        activity.setReducenum(1);

        float price = PriceCalculator.calculateNewPrice(goods, activity);
        Assert.assertEquals(200f, price, 0.001); // 满3件减1件，相当于买2件

        // 边界测试：不满条件
        goods.setNum(2);
        price = PriceCalculator.calculateNewPrice(goods, activity);
        Assert.assertEquals(200f, price, 0.001); // 不满足满3件，原价
    }

    @Test
    public void testFullPriceReductionActivity() {
        Goods goods = new Goods();
        goods.setPrice(100f);
        goods.setNum(3);

        Activity activity = new Activity();
        activity.setDiscount(1.0f);
        activity.setFullprice(250);
        activity.setReduceprice(50);

        float price = PriceCalculator.calculateNewPrice(goods, activity);
        Assert.assertEquals(250f, price, 0.001); // 总价300，满250减50，最终250

        // 边界测试：刚满条件
        goods.setPrice(125f);
        goods.setNum(2); // 250
        price = PriceCalculator.calculateNewPrice(goods, activity);
        Assert.assertEquals(200f, price, 0.001); // 满250减50，最终200

        // 边界测试：不满条件
        goods.setPrice(100f);
        goods.setNum(2); // 200
        price = PriceCalculator.calculateNewPrice(goods, activity);
        Assert.assertEquals(200f, price, 0.001); // 不满250，原价
    }

    @Test
    public void testBoundaryValues() {
        Goods goods = new Goods();
        goods.setPrice(100f);
        goods.setNum(1);

        Activity activity = new Activity();
        activity.setDiscount(1.0f);
        activity.setFullprice(50);
        activity.setReduceprice(200); // 减的钱比总价多

        float price = PriceCalculator.calculateNewPrice(goods, activity);
        Assert.assertEquals(0f, price, 0.001); // 价格不能为负数
        
        // 满件减边界
        activity = new Activity();
        activity.setFullnum(1);
        activity.setReducenum(2); // 减免件数大于购买件数
        
        price = PriceCalculator.calculateNewPrice(goods, activity);
        Assert.assertEquals(0f, price, 0.001); // 价格不能为负数
    }
}
