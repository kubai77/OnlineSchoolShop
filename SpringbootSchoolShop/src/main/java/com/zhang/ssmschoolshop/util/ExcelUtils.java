package com.zhang.ssmschoolshop.util;

/**
 * @author codingzx
 * @description
 * @date 2021/4/11 11:23
 */

import com.zhang.ssmschoolshop.annotinon.ExportEntityMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : ExportExcelUtils2
 * @Description :
 * @Author : ASUS
 * @Date 2020/3/18
 * @Version 1.0
 **/
@Slf4j
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 导出Excel
     *
     * @param excelName 要导出的excel名称
     * @param list      要导出的数据集合
     * @param c         中英文字段对应Map，即要导出的excel表头
     * @param response  使用response可以导出到浏览器
     * @param <T>
     */
    public static <T> void export(String excelName, List<T> list, Class<T> c, HttpServletResponse response) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
        if (!StringUtils.hasText(excelName)) {
            excelName = df.format(new Date());
        } else {
            excelName = excelName + df.format(new Date());
        }
        response.reset();
        response.setContentType("application/vnd.ms-excel");
        try {
            response.setHeader("Content-disposition", "attachment; filename="
                    + new String(excelName.getBytes("gb2312"), "ISO-8859-1") + ".xls");
        } catch (UnsupportedEncodingException e) {
            logger.error("设置导出文件名失败", e);
        }

        try (HSSFWorkbook wb = new HSSFWorkbook(); OutputStream outputStream = response.getOutputStream()) {
            HSSFSheet sheet = wb.createSheet(excelName);
            HSSFPrintSetup printSetup = sheet.getPrintSetup();
            printSetup.setLandscape(true);
            printSetup.setHeaderMargin(0.2);
            printSetup.setFooterMargin(0.2);
            printSetup.setFitHeight((short) 0);
            printSetup.setFitWidth((short) 1);
            printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
            sheet.setMargin(HSSFSheet.BottomMargin, (double) 0.8);
            sheet.setMargin(HSSFSheet.LeftMargin, (double) 0);
            sheet.setMargin(HSSFSheet.RightMargin, (double) 0);
            sheet.setMargin(HSSFSheet.TopMargin, (double) 0.8);
            sheet.setHorizontallyCenter(true);
            sheet.setVerticallyCenter(true);
            sheet.setAutobreaks(false);
            sheet.setFitToPage(false);
            Footer footer = sheet.getFooter();
            footer.setCenter("第" + HeaderFooter.page() + "页，共 " + HeaderFooter.numPages() + "页");
            Header header = sheet.getHeader();
            header.setLeft(HSSFHeader.font("宋体", "") + HSSFHeader.fontSize((short) 16) + excelName + ".xls");

            HSSFCellStyle style = wb.createCellStyle();
            HSSFFont font = wb.createFont();
            font.setFontName("宋体");
            style.setFont(font);

            LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
            Field[] declaredFields = c.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                ExportEntityMap declaredAnnotation = declaredField.getDeclaredAnnotation(ExportEntityMap.class);
                if (declaredAnnotation != null) {
                    fieldMap.put(declaredAnnotation.EnName(), declaredAnnotation.CnName());
                }
            }
            fillSheet(sheet, list, fieldMap, style);
            wb.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            logger.error("导出Excel失败", e);
        }
    }

    /**
     * 根据字段名获取字段对象
     *
     * @param fieldName 字段名
     * @param clazz     包含该字段的类
     * @return 字段
     */
    public static Field getFieldByName(String fieldName, Class<?> clazz) {
        logger.info("根据字段名获取字段对象:getFieldByName()");
        Field[] selfFields = clazz.getDeclaredFields();
        for (Field field : selfFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return getFieldByName(fieldName, superClazz);
        }
        return null;
    }

    /**
     * 根据字段名获取字段值
     *
     * @param fieldName 字段名
     * @param o         对象
     * @return 字段值
     * @throws Exception 异常
     */
    public static Object getFieldValueByName(String fieldName, Object o)
            throws Exception {
        Object value = null;
        Field field = getFieldByName(fieldName, o.getClass());
        if (field != null) {
            field.setAccessible(true);
            value = field.get(o);
        } else {
            throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
        }
        return value;
    }

    /**
     * 根据带路径或不带路径的属性名获取属性值,即接受简单属性名，
     * 如userName等，又接受带路径的属性名，如student.department.name等
     *
     * @param fieldNameSequence 带路径的属性名或简单属性名
     * @param o                 对象
     * @return 属性值
     * @throws Exception 异常
     */
    public static Object getFieldValueByNameSequence(String fieldNameSequence,
                                                     Object o) throws Exception {
        Object value = null;
        String[] attributes = fieldNameSequence.split("\\.");
        if (attributes.length == 1) {
            value = getFieldValueByName(fieldNameSequence, o);
        } else {
            Object fieldObj = getFieldValueByName(attributes[0], o);
            String subFieldNameSequence = fieldNameSequence.substring(fieldNameSequence.indexOf(".") + 1);
            value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value;
    }

    /**
     * 向工作表中填充数据
     *
     * @param sheet    excel的工作表名称
     * @param list     数据源
     * @param fieldMap 中英文字段对应关系的Map
     * @param style    表格中的格式
     * @throws Exception 异常
     */
    public static <T> void fillSheet(HSSFSheet sheet, List<T> list,
                                     LinkedHashMap<String, String> fieldMap, HSSFCellStyle style) throws Exception {
        String[] enFields = new String[fieldMap.size()];
        String[] cnFields = new String[fieldMap.size()];
        int count = 0;
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            enFields[count] = entry.getKey();
            cnFields[count] = entry.getValue();
            count++;
        }
        Map<Integer, Integer> maxWidth = new HashMap<>();
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCell cell = null;
        for (int i = 0; i < cnFields.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(cnFields[i]);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
            maxWidth.put(i, cell.getStringCellValue().getBytes().length * 256 + 200);
        }
        for (int index = 0; index < list.size(); index++) {
            row = sheet.createRow(index + 1);
            T item = list.get(index);
            int j = 0;
            for (int i = 0; i < enFields.length; i++) {
                HSSFCell createCell = row.createCell(j);
                Object objValue = getFieldValueByNameSequence(enFields[i], item);
                String fieldValue = objValue == null ? "" : objValue.toString();
                cell = row.createCell(i);
                createCell.setCellValue(fieldValue);

                int length = createCell.getStringCellValue().getBytes().length * 256 + 200;
                if (length > 15000) {
                    length = 15000;
                }
                maxWidth.put(j, Math.max(length, maxWidth.get(j)));
                j++;
                createCell.setCellStyle(style);
            }
        }

        for (int i = 0; i < cnFields.length; i++) {
            sheet.setColumnWidth(i, maxWidth.get(i));
        }
    }

    public static <T> List<T> getRecordByTxt(String path) {
        List<T> result = new ArrayList<>();
        try {
            List<String> contentList = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            contentList.forEach(e -> {
                if (!StringUtils.isEmpty(e)) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
