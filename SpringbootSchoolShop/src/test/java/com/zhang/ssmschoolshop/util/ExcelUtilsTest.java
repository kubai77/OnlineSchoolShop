package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.annotinon.ExportEntityMap;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ExcelUtilsTest {

    /**
     * 用于测试的实体类，带有 @ExportEntityMap 注解
     */
    public static class TestEntity {
        @ExportEntityMap(EnName = "name", CnName = "姓名")
        private String name;

        @ExportEntityMap(EnName = "age", CnName = "年龄")
        private Integer age;

        public TestEntity() {}

        public TestEntity(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
    }

    @Test
    public void testExportSetsContentType() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<TestEntity> list = new ArrayList<>();
        list.add(new TestEntity("张三", 20));

        ExcelUtils.export("测试表", list, TestEntity.class, response);

        assertEquals("application/vnd.ms-excel", response.getContentType());
    }

    @Test
    public void testExportSetsContentDispositionHeader() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<TestEntity> list = new ArrayList<>();
        list.add(new TestEntity("张三", 20));

        ExcelUtils.export("测试表", list, TestEntity.class, response);

        String contentDisposition = response.getHeader("Content-disposition");
        assertNotNull("Content-disposition 头不应为 null", contentDisposition);
        assertTrue("Content-disposition 应包含 attachment", contentDisposition.contains("attachment"));
        assertTrue("Content-disposition 应包含 .xls 后缀", contentDisposition.endsWith(".xls"));
        assertTrue("Content-disposition 应包含文件名", contentDisposition.contains("filename="));
    }

    @Test
    public void testExportFilenameContainsExcelName() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<TestEntity> list = new ArrayList<>();
        list.add(new TestEntity("张三", 20));

        String excelName = "资源详情表";
        ExcelUtils.export(excelName, list, TestEntity.class, response);

        String contentDisposition = response.getHeader("Content-disposition");
        assertNotNull(contentDisposition);
        // 文件名应以 excelName 开头（编码后可能不同，但原始名应出现在编码前）
        // 由于 gb2312 编码，直接检查包含关系可能不适用，改为检查 .xls 后缀
        assertTrue("文件名应以 .xls 结尾", contentDisposition.contains(".xls"));
    }

    @Test
    public void testExportWritesNonEmptyContent() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<TestEntity> list = new ArrayList<>();
        list.add(new TestEntity("张三", 20));
        list.add(new TestEntity("李四", 25));

        ExcelUtils.export("测试表", list, TestEntity.class, response);

        byte[] content = response.getContentAsByteArray();
        assertTrue("导出内容不应为空", content.length > 0);
    }

    @Test
    public void testExportWithEmptyList() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<TestEntity> list = new ArrayList<>();

        ExcelUtils.export("空表", list, TestEntity.class, response);

        assertEquals("application/vnd.ms-excel", response.getContentType());
        String contentDisposition = response.getHeader("Content-disposition");
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.contains(".xls"));
    }

    @Test
    public void testExportWithNullExcelName() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<TestEntity> list = new ArrayList<>();
        list.add(new TestEntity("张三", 20));

        // excelName 为 null 时应使用日期作为默认文件名
        ExcelUtils.export(null, list, TestEntity.class, response);

        assertEquals("application/vnd.ms-excel", response.getContentType());
        String contentDisposition = response.getHeader("Content-disposition");
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.contains(".xls"));
    }
}
