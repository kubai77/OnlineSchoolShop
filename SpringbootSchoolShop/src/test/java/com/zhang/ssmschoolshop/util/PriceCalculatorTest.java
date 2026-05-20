package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.entity.Activity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * PriceCalculator 单元测试。
 * 覆盖场景：无活动、折扣活动、满件减、满额减、边界值。
 */
public class PriceCalculatorTest {

    // ==================== 辅助方法 ====================

    private static Activity newActivity() {
        return new Activity();
    }

    private static Activity discountActivity(float discount) {
        Activity a = new Activity();
        a.setDiscount(discount);
        return a;
    }

    private static Activity fullNumActivity(int fullnum, int reducenum) {
        Activity a = new Activity();
        a.setFullnum(fullnum);
        a.setReducenum(reducenum);
        return a;
    }

    private static Activity fullPriceActivity(int fullprice, int reduceprice) {
        Activity a = new Activity();
        a.setFullprice(fullprice);
        a.setReduceprice(reduceprice);
        return a;
    }

    // ==================== 无活动 ====================

    @Test
    public void shouldReturnOriginalPriceWhenActivityIsNull() {
        // activity == null → 原价
        assertEquals(100f, PriceCalculator.calculate(10, 10, null), 0.001);
    }

    @Test
    public void shouldReturnOriginalPriceWhenActivityHasNoEffectiveFields() {
        // 空 Activity（所有优惠字段均为 null）→ 原价
        assertEquals(50f, PriceCalculator.calculate(10, 5, newActivity()), 0.001);
    }

    @Test
    public void shouldReturnOriginalPriceWhenDiscountIsOne() {
        // discount == 1.0 → 不视为折扣活动
        assertEquals(30f, PriceCalculator.calculate(10, 3, discountActivity(1.0f)), 0.001);
    }

    // ==================== 折扣活动 ====================

    @Test
    public void shouldApplyDiscountCorrectly() {
        // 单价 10, 数量 5, 8 折 → 10*5*0.8 = 40
        assertEquals(40f, PriceCalculator.calculate(10, 5, discountActivity(0.8f)), 0.001);
    }

    @Test
    public void shouldApplyDiscountWithSingleItem() {
        // 单价 100, 数量 1, 85 折 → 85
        assertEquals(85f, PriceCalculator.calculate(100, 1, discountActivity(0.85f)), 0.001);
    }

    @Test
    public void shouldApplyDiscountWithLargeQuantity() {
        // 单价 50, 数量 20, 7 折 → 50*20*0.7 = 700
        assertEquals(700f, PriceCalculator.calculate(50, 20, discountActivity(0.7f)), 0.001);
    }

    @Test
    public void discountActivityShouldTakePriorityOverOthers() {
        // 同时设置 discount=0.5 和 fullnum=3/reducenum=1 →
        // 折扣优先，应为 10*10*0.5=50，而非满件减结果
        Activity a = new Activity();
        a.setDiscount(0.5f);
        a.setFullnum(3);
        a.setReducenum(1);
        assertEquals(50f, PriceCalculator.calculate(10, 10, a), 0.001);
    }

    // ==================== 满件减 ====================

    @Test
    public void shouldApplyFullNumReductionWhenThresholdMet() {
        // 单价 10, 数量 5, 满 3 件减 1 件 → 10*(5-1) = 40
        assertEquals(40f, PriceCalculator.calculate(10, 5, fullNumActivity(3, 1)), 0.001);
    }

    @Test
    public void shouldNotApplyFullNumReductionWhenBelowThreshold() {
        // 单价 10, 数量 2, 满 3 件减 1 件 → 不满足条件, 10*2=20
        assertEquals(20f, PriceCalculator.calculate(10, 2, fullNumActivity(3, 1)), 0.001);
    }

    @Test
    public void shouldApplyFullNumReductionAtExactThreshold() {
        // 单价 10, 数量 3, 满 3 件减 1 件 → 刚好满足, 10*(3-1)=20
        assertEquals(20f, PriceCalculator.calculate(10, 3, fullNumActivity(3, 1)), 0.001);
    }

    @Test
    public void shouldHandleFullNumWithMoreThanOneFreeItem() {
        // 单价 20, 数量 10, 满 5 件减 3 件 → 20*(10-3)=140
        assertEquals(140f, PriceCalculator.calculate(20, 10, fullNumActivity(5, 3)), 0.001);
    }

    @Test
    public void shouldClampToZeroWhenReducenumExceedsQuantity() {
        // reducenum 超过购买数量，实付不能为负 → 0
        assertEquals(0f, PriceCalculator.calculate(10, 1, fullNumActivity(1, 5)), 0.001);
    }

    // ==================== 满额减 ====================

    @Test
    public void shouldApplyFullPriceReductionWhenThresholdMet() {
        // 单价 50, 数量 2, 满 80 减 15 → 50*2=100>=80, 100-15=85
        assertEquals(85f, PriceCalculator.calculate(50, 2, fullPriceActivity(80, 15)), 0.001);
    }

    @Test
    public void shouldNotApplyFullPriceReductionWhenBelowThreshold() {
        // 单价 10, 数量 3, 满 50 减 10 → 30<50, 30
        assertEquals(30f, PriceCalculator.calculate(10, 3, fullPriceActivity(50, 10)), 0.001);
    }

    @Test
    public void shouldApplyFullPriceReductionAtExactThreshold() {
        // 单价 25, 数量 4, 满 100 减 20 → 100>=100, 100-20=80
        assertEquals(80f, PriceCalculator.calculate(25, 4, fullPriceActivity(100, 20)), 0.001);
    }

    @Test
    public void shouldClampToZeroWhenReducepriceExceedsTotal() {
        // 单价 10, 数量 1, 满 5 减 50 → 10>=5, 10-50=-40 → 0
        assertEquals(0f, PriceCalculator.calculate(10, 1, fullPriceActivity(5, 50)), 0.001);
    }

    // ==================== 边界值 ====================

    @Test
    public void shouldReturnZeroWhenQuantityIsZero() {
        assertEquals(0f, PriceCalculator.calculate(100, 0, discountActivity(0.5f)), 0.001);
        assertEquals(0f, PriceCalculator.calculate(100, 0, fullNumActivity(3, 1)), 0.001);
        assertEquals(0f, PriceCalculator.calculate(100, 0, fullPriceActivity(50, 10)), 0.001);
        assertEquals(0f, PriceCalculator.calculate(100, 0, null), 0.001);
    }

    @Test
    public void shouldReturnZeroWhenUnitPriceIsZero() {
        assertEquals(0f, PriceCalculator.calculate(0, 10, discountActivity(0.5f)), 0.001);
        assertEquals(0f, PriceCalculator.calculate(0, 10, fullNumActivity(3, 1)), 0.001);
        assertEquals(0f, PriceCalculator.calculate(0, 10, fullPriceActivity(50, 10)), 0.001);
        assertEquals(0f, PriceCalculator.calculate(0, 10, null), 0.001);
    }

    @Test
    public void shouldReturnZeroWhenBothPriceAndQuantityAreZero() {
        assertEquals(0f, PriceCalculator.calculate(0, 0, discountActivity(0.8f)), 0.001);
    }

    @Test
    public void shouldHandleNegativeQuantityGracefully() {
        // quantity <= 0 → 返回 0
        assertEquals(0f, PriceCalculator.calculate(50, -1, discountActivity(0.9f)), 0.001);
    }

    // ==================== 类型判断辅助方法 ====================

    @Test
    public void isDiscountActivityShouldDetectCorrectly() {
        assertTrue(PriceCalculator.isDiscountActivity(discountActivity(0.8f)));
        assertFalse(PriceCalculator.isDiscountActivity(discountActivity(1.0f)));
        assertFalse(PriceCalculator.isDiscountActivity(newActivity())); // discount == null
    }

    @Test
    public void isFullNumActivityShouldDetectCorrectly() {
        assertTrue(PriceCalculator.isFullNumActivity(fullNumActivity(3, 1)));
        Activity onlyFullnum = new Activity();
        onlyFullnum.setFullnum(3);
        assertFalse(PriceCalculator.isFullNumActivity(onlyFullnum));
        Activity onlyReducenum = new Activity();
        onlyReducenum.setReducenum(1);
        assertFalse(PriceCalculator.isFullNumActivity(onlyReducenum));
        assertFalse(PriceCalculator.isFullNumActivity(newActivity()));
    }

    @Test
    public void isFullPriceActivityShouldDetectCorrectly() {
        assertTrue(PriceCalculator.isFullPriceActivity(fullPriceActivity(100, 20)));
        Activity onlyFullprice = new Activity();
        onlyFullprice.setFullprice(100);
        assertFalse(PriceCalculator.isFullPriceActivity(onlyFullprice));
        Activity onlyReduceprice = new Activity();
        onlyReduceprice.setReduceprice(20);
        assertFalse(PriceCalculator.isFullPriceActivity(onlyReduceprice));
        assertFalse(PriceCalculator.isFullPriceActivity(newActivity()));
    }
}