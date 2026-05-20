package com.zhang.ssmschoolshop.controller.admin;

import com.zhang.ssmschoolshop.entity.Goods;
import com.zhang.ssmschoolshop.entity.GoodsExample;
import com.zhang.ssmschoolshop.service.GoodsService;
import com.zhang.ssmschoolshop.util.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author codingzx
 * @description
 * @date 2021/7/24 13:20
 */
@Controller
@RequestMapping("/admin")
public class ExcelController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/excel/export")
    public void exportRecord(HttpServletResponse response) {
        List<Goods> goodsList = goodsService.selectByExample(new GoodsExample());
        String excelName = "资源详情表";
        ExcelUtils.export(excelName, goodsList, Goods.class, response);
    }

    @GetMapping("/excel/import")
    public String ImportRecord() {
        return "导入资源成功";
    }
}
