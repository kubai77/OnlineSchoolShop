package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.entity.Goods;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ExcelUtilsTest {

    @Test
    public void testExport() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String excelName = "测试导出";
        List<Goods> goodsList = new ArrayList<>();

        Goods goods1 = new Goods();
        goods1.setGoodsid(1);
        goods1.setGoodsname("测试商品1");
        goods1.setPrice(100);
        goods1.setNum(10);
        goods1.setUptime(new Date());
        goods1.setCategory(1);
        goods1.setDetailcate("测试分类1");
        goods1.setActivityid(1);
        goods1.setDescription("测试描述1");

        Goods goods2 = new Goods();
        goods2.setGoodsid(2);
        goods2.setGoodsname("测试商品2");
        goods2.setPrice(200);
        goods2.setNum(20);
        goods2.setUptime(new Date());
        goods2.setCategory(2);
        goods2.setDetailcate("测试分类2");
        goods2.setActivityid(2);
        goods2.setDescription("测试描述2");

        goodsList.add(goods1);
        goodsList.add(goods2);

        ExcelUtils.export(excelName, goodsList, Goods.class, response);

        assertEquals("application/vnd.ms-excel", response.getContentType());
        assertTrue(response.getHeader("Content-disposition").contains("测试导出"));
        assertTrue(response.getHeader("Content-disposition").endsWith(".xls"));
        assertNotNull(response.getContentAsByteArray());
        assertTrue(response.getContentAsByteArray().length > 0);
    }
}
