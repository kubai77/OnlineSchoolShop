package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Goods;
import org.junit.Test;

import static org.junit.Assert.*;

public class PriceCalculatorTest {

    /**
     * 场景1：无活动
     */
    @Test
    public void testNoActivity() {
        Goods goods = createGoods(100, 5);
        Activity activity = null;

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(500f, result, 0.01f);
    }

    /**
     * 场景2：折扣活动
     */
    @Test
    public void testDiscountActivity() {
        Goods goods = createGoods(100, 5);
        Activity activity = new Activity();
        activity.setDiscount(0.8f); // 8折

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(400f, result, 0.01f); // 100*5*0.8 = 400
    }

    /**
     * 场景3：满件减（满足条件）
     */
    @Test
    public void testFullNumReduce_Satisfied() {
        Goods goods = createGoods(50, 10); // 10件，每件50元
        Activity activity = new Activity();
        activity.setFullnum(5);  // 满5件
        activity.setReducenum(2); // 减2件的钱（即减100元）

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(400f, result, 0.01f); // 10*50 - 2*50 = 400
    }

    /**
     * 场景3扩展：满件减（不满足条件）
     */
    @Test
    public void testFullNumReduce_NotSatisfied() {
        Goods goods = createGoods(50, 3); // 只有3件，不满足满5件条件
        Activity activity = new Activity();
        activity.setFullnum(5);
        activity.setReducenum(2);

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(150f, result, 0.01f); // 3*50 = 150，原价
    }

    /**
     * 场景4：满额减（满足条件）
     */
    @Test
    public void testFullPriceReduce_Satisfied() {
        Goods goods = createGoods(100, 5); // 500元
        Activity activity = new Activity();
        activity.setFullprice(300);  // 满300元
        activity.setReduceprice(50);  // 减50元

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(450f, result, 0.01f); // 500 - 50 = 450
    }

    /**
     * 场景4扩展：满额减（不满足条件）
     */
    @Test
    public void testFullPriceReduce_NotSatisfied() {
        Goods goods = createGoods(100, 2); // 200元，不满足满300条件
        Activity activity = new Activity();
        activity.setFullprice(300);
        activity.setReduceprice(50);

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(200f, result, 0.01f); // 2*100 = 200，原价
    }

    /**
     * 场景5：边界值测试
     */
    @Test
    public void testBoundary_FullNumExactly() {
        // 刚好满足满件条件
        Goods goods = createGoods(30, 5);
        Activity activity = new Activity();
        activity.setFullnum(5);
        activity.setReducenum(1);

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(120f, result, 0.01f); // 5*30 - 1*30 = 120
    }

    @Test
    public void testBoundary_FullPriceExactly() {
        // 刚好满足满额条件
        Goods goods = createGoods(100, 3); // 300元，刚好
        Activity activity = new Activity();
        activity.setFullprice(300);
        activity.setReduceprice(30);

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(270f, result, 0.01f); // 300 - 30 = 270
    }

    @Test
    public void testBoundary_ZeroValues() {
        // 边界值：价格为0
        Goods goods = new Goods();
        goods.setPrice(0);
        goods.setNum(5);
        Activity activity = new Activity();
        activity.setDiscount(0.5f);

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(0f, result, 0.01f);
    }

    @Test
    public void testBoundary_NullFields() {
        // 边界值：activity字段为null
        Goods goods = createGoods(100, 5);
        Activity activity = new Activity();
        activity.setDiscount(1.0f); // 无折扣

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(500f, result, 0.01f);
    }

    @Test
    public void testDiscount_FullDiscount() {
        // 边界值：1折（最大折扣）
        Goods goods = createGoods(100, 1);
        Activity activity = new Activity();
        activity.setDiscount(0.1f);

        float result = PriceCalculator.calculate(goods, activity);

        assertEquals(10f, result, 0.01f);
    }

    // 辅助方法：创建Goods对象
    private Goods createGoods(int price, int num) {
        Goods goods = new Goods();
        goods.setPrice(price);
        goods.setNum(num);
        return goods;
    }
}