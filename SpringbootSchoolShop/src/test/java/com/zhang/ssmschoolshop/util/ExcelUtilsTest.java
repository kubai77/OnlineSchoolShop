package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.annotinon.ExportEntityMap;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * ExcelUtils 单元测试，验证导出时的响应头、文件名和内容类型。
 */
public class ExcelUtilsTest {

    /**
     * 测试用实体，使用 @ExportEntityMap 标注导出字段。
     */
    public static class TestEntity {

        @ExportEntityMap(EnName = "id", CnName = "编号")
        private Integer id;

        @ExportEntityMap(EnName = "name", CnName = "名称")
        private String name;

        public TestEntity() {
        }

        public TestEntity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testExportResponseHeadersAndContentType() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        List<TestEntity> dataList = new ArrayList<>();
        dataList.add(new TestEntity(1, "测试商品A"));
        dataList.add(new TestEntity(2, "测试商品B"));

        String excelName = "测试导出";
        ExcelUtils.export(excelName, dataList, TestEntity.class, response);

        // 1. 验证 Content-Type
        assertEquals("application/vnd.ms-excel", response.getContentType());

        // 2. 验证 Content-Disposition 头存在且格式正确
        String contentDisposition = response.getHeader("Content-disposition");
        assertNotNull("Content-disposition 头不能为空", contentDisposition);
        assertTrue("Content-disposition 应包含 attachment",
                contentDisposition.contains("attachment"));

        // 文件名经过 gb2312→ISO-8859-1 编码，验证能正确解码回原始名称
        String filenamePart = contentDisposition.substring(
                contentDisposition.indexOf("filename=") + "filename=".length());
        // 去掉首尾引号
        if (filenamePart.startsWith("\"")) {
            filenamePart = filenamePart.substring(1, filenamePart.length() - 1);
        }
        String decodedName = new String(filenamePart.getBytes("ISO-8859-1"), "gb2312");
        assertTrue("解码后文件名应包含原始 excelName",
                decodedName.contains(excelName));

        // 3. 验证响应体非空
        byte[] content = response.getContentAsByteArray();
        assertNotNull("响应体不能为空", content);
        assertTrue("响应体应包含数据", content.length > 0);

        // 4. 验证文件名包含 .xls 后缀
        assertTrue("文件名应以 .xls 结尾", decodedName.endsWith(".xls"));
    }

    @Test
    public void testExportWithEmptyList() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        List<TestEntity> dataList = new ArrayList<>();

        ExcelUtils.export("空列表导出", dataList, TestEntity.class, response);

        // Content-Type 仍然正确
        assertEquals("application/vnd.ms-excel", response.getContentType());

        // Content-Disposition 仍然存在
        assertNotNull(response.getHeader("Content-disposition"));

        // 即使数据为空，也应生成含表头的 Excel
        byte[] content = response.getContentAsByteArray();
        assertNotNull(content);
        assertTrue("即使是空数据也应生成表头", content.length > 0);
    }

    @Test
    public void testExportNullExcelNameUsesDefault() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        List<TestEntity> dataList = new ArrayList<>();
        dataList.add(new TestEntity(1, "test"));

        // 传入 null 作为文件名
        ExcelUtils.export(null, dataList, TestEntity.class, response);

        assertEquals("application/vnd.ms-excel", response.getContentType());

        String contentDisposition = response.getHeader("Content-disposition");
        assertNotNull(contentDisposition);
        // null 时文件名应为当前日期时间格式
        assertTrue("文件名应以 .xls 结尾",
                contentDisposition.endsWith(".xls\"") || contentDisposition.contains(".xls"));

        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);
    }
}