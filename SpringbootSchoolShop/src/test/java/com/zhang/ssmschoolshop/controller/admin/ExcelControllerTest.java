package com.zhang.ssmschoolshop.controller.admin;

import com.zhang.ssmschoolshop.entity.Goods;
import com.zhang.ssmschoolshop.entity.GoodsExample;
import com.zhang.ssmschoolshop.service.GoodsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ExcelControllerTest {

    @Mock
    private GoodsService goodsService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ExcelController controller = new ExcelController();
        ReflectionTestUtils.setField(controller, "goodsService", goodsService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void exportRecordShouldUseVoidReturnType() throws Exception {
        Method method = ExcelController.class.getMethod("exportRecord", HttpServletResponse.class);
        assertEquals(Void.TYPE, method.getReturnType());
    }

    @Test
    public void exportEndpointShouldWriteExcelResponse() throws Exception {
        when(goodsService.selectByExample(any(GoodsExample.class))).thenReturn(Collections.singletonList(new Goods()));

        MvcResult result = mockMvc.perform(get("/admin/excel/export"))
                .andExpect(status().isOk())
                .andReturn();

        assertNull(result.getModelAndView());

        MockHttpServletResponse response = result.getResponse();
        assertEquals("application/vnd.ms-excel", response.getContentType());
        String contentDisposition = response.getHeader("Content-disposition");
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.startsWith("attachment; filename="));
        assertTrue(contentDisposition.endsWith(".xls"));

        String encodedFileName = contentDisposition.substring("attachment; filename=".length(), contentDisposition.length() - 4);
        String decodedFileName = new String(encodedFileName.getBytes(StandardCharsets.ISO_8859_1), "gb2312");
        assertTrue(decodedFileName.startsWith("资源详情表"));
        assertTrue(response.getContentAsByteArray().length > 0);
    }
}
