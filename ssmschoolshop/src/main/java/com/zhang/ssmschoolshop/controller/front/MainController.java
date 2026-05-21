package com.zhang.ssmschoolshop.controller.front;


import com.zhang.ssmschoolshop.entity.Category;
import com.zhang.ssmschoolshop.entity.CategoryExample;
import com.zhang.ssmschoolshop.entity.Goods;
import com.zhang.ssmschoolshop.entity.GoodsExample;
import com.zhang.ssmschoolshop.entity.User;
import com.zhang.ssmschoolshop.service.CateService;
import com.zhang.ssmschoolshop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private CateService cateService;

    @Autowired
    private GoodsService goodsService;


    @RequestMapping("/")
    public String showAdmin(Model model, HttpSession session) {
        loadMainGoods(model, session);
        return "main";
    }




    @RequestMapping("/main")
    public String showAllGoods(Model model, HttpSession session) {
        loadMainGoods(model, session);
        return "main";
    }

    public List<Goods> getCateGoods(String cate, Integer userid) {
        CategoryExample digCategoryExample = new CategoryExample();
        digCategoryExample.or().andCatenameLike(cate);
        List<Category> digCategoryList = cateService.selectByExample(digCategoryExample);

        if (digCategoryList.size() == 0) {
            return null;
        }

        GoodsExample digGoodsExample = new GoodsExample();
        List<Integer> digCateId = new ArrayList<Integer>();
        for (Category tmp:digCategoryList) {
            digCateId.add(tmp.getCateid());
        }
        digGoodsExample.or().andCategoryIn(digCateId);

        List<Goods> goodsList = goodsService.selectByExampleLimit(digGoodsExample);
        return goodsService.enrichGoodsList(goodsList, userid);
    }

    private void loadMainGoods(Model model, HttpSession session) {
        Integer userid = getUserid(session);
        model.addAttribute("digGoods", getCateGoods("数码", userid));
        model.addAttribute("houseGoods", getCateGoods("家电", userid));
        model.addAttribute("colGoods", getCateGoods("服饰", userid));
        model.addAttribute("bookGoods", getCateGoods("书籍", userid));
    }

    private Integer getUserid(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return null;
        }
        return user.getUserid();
    }
}
