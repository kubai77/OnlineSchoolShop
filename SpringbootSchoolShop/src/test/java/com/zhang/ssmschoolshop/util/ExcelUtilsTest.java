package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.entity.Goods;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExcelUtilsTest {

    @Test
    public void testExport() throws UnsupportedEncodingException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<Goods> list = new ArrayList<>();
        Goods goods = new Goods();
        goods.setGoodsname("Test Goods");
        list.add(goods);

        String excelName = "测试资源详情表";

        ExcelUtils.export(excelName, list, Goods.class, response);

        // 验证 Content-Type
        assertEquals("application/vnd.ms-excel", response.getContentType());

        // 验证 Content-Disposition
        String contentDisposition = response.getHeader("Content-disposition");
        assertNotNull("Content-disposition header should not be null", contentDisposition);
        assertTrue("Content-disposition should contain attachment", contentDisposition.contains("attachment;"));
        
        // 验证文件名是否包含我们设定的 excelName（经过 ISO-8859-1 编码）
        String expectedFileNamePart = new String(excelName.getBytes("gb2312"), "ISO-8859-1");
        assertTrue("Filename should contain the encoded excel name", contentDisposition.contains(expectedFileNamePart));
        assertTrue("Filename should end with .xls", contentDisposition.endsWith(".xls"));

        // 验证文件内容是否被写入
        assertTrue("Response content should not be empty", response.getContentAsByteArray().length > 0);
    }
}
