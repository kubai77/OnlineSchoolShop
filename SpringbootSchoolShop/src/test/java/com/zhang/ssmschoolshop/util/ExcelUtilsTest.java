package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.annotinon.ExportEntityMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExcelUtilsTest {

    @Test
    public void exportShouldWriteExpectedHeadersAndExcelBody() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        ExcelUtils.export("资源详情表", Collections.singletonList(new ExportSample("Java 编程", 99)), ExportSample.class, response);

        assertEquals("application/vnd.ms-excel", response.getContentType());
        String contentDisposition = response.getHeader("Content-disposition");
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.startsWith("attachment; filename="));
        assertTrue(contentDisposition.endsWith(".xls"));

        String encodedFileName = contentDisposition.substring("attachment; filename=".length(), contentDisposition.length() - 4);
        String decodedFileName = new String(encodedFileName.getBytes(StandardCharsets.ISO_8859_1), "gb2312");
        assertTrue(decodedFileName.startsWith("资源详情表"));
        assertTrue(response.getContentAsByteArray().length > 0);

        try (HSSFWorkbook workbook = new HSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            assertEquals(1, workbook.getNumberOfSheets());
            assertTrue(workbook.getSheetName(0).startsWith("资源详情表"));
        }
    }

    private static class ExportSample {
        @ExportEntityMap(EnName = "name", CnName = "名称")
        private final String name;

        @ExportEntityMap(EnName = "price", CnName = "价格")
        private final Integer price;

        private ExportSample(String name, Integer price) {
            this.name = name;
            this.price = price;
        }
    }
}
