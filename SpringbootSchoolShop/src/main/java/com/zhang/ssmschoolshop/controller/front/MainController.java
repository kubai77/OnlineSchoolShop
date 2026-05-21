package com.zhang.ssmschoolshop.controller.front;


import com.zhang.ssmschoolshop.entity.*;
import com.zhang.ssmschoolshop.service.CateService;
import com.zhang.ssmschoolshop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private CateService cateService;

    @Autowired
    private GoodsService goodsService;


    @RequestMapping("/")
    public String showAdmin(Model model, HttpSession session) {
        Integer userid;
        User user = (User) session.getAttribute("user");
        if (user == null) {
            userid = null;
        } else {
            userid = user.getUserid();
        }

        //数码分类
        List<Goods> digGoods = getCateGoods("数码", userid);
        model.addAttribute("digGoods", digGoods);

        //家电
        List<Goods> houseGoods = getCateGoods("家电", userid);
        model.addAttribute("houseGoods", houseGoods);

        //服饰
        List<Goods> colGoods = getCateGoods("服饰", userid);
        model.addAttribute("colGoods", colGoods);

        //书籍
        List<Goods> bookGoods = getCateGoods("书籍", userid);
        model.addAttribute("bookGoods", bookGoods);

        return "main";
    }




    @RequestMapping("/main")
    public String showAllGoods(Model model, HttpSession session) {
        Integer userid;
        User user = (User) session.getAttribute("user");
        if (user == null) {
            userid = null;
        } else {
            userid = user.getUserid();
        }
        //数码分类
        List<Goods> digGoods = getCateGoods("数码", userid);
        model.addAttribute("digGoods", digGoods);
        //家电
        List<Goods> houseGoods = getCateGoods("家电", userid);
        model.addAttribute("houseGoods", houseGoods);
        //服饰
        List<Goods> colGoods = getCateGoods("服饰", userid);
        model.addAttribute("colGoods", colGoods);
        //书籍
        List<Goods> bookGoods = getCateGoods("书籍", userid);
        model.addAttribute("bookGoods", bookGoods);

        return "main";
    }

    public List<Goods> getCateGoods(String cate, Integer userid) {
        //查询分类
        CategoryExample digCategoryExample = new CategoryExample();
        digCategoryExample.or().andCatenameLike(cate);
        List<Category> digCategoryList = cateService.selectByExample(digCategoryExample);

        if (digCategoryList.size() == 0) {
            return null;
        }

        //查询属于刚查到的分类的商品
        GoodsExample digGoodsExample = new GoodsExample();
        List<Integer> digCateId = new ArrayList<Integer>();
        for (Category tmp:digCategoryList) {
            digCateId.add(tmp.getCateid());
        }
        digGoodsExample.or().andCategoryIn(digCateId);

        List<Goods> goodsList = goodsService.selectByExampleLimit(digGoodsExample);

        if (goodsList == null || goodsList.isEmpty()) {
            return goodsList;
        }

        // 批量查询图片
        List<Integer> goodsIds = goodsList.stream()
                .map(Goods::getGoodsid)
                .collect(Collectors.toList());
        List<ImagePath> allImages = goodsService.findImagePathByGoodsIds(goodsIds);
        Map<Integer, List<ImagePath>> imageMap = allImages.stream()
                .collect(Collectors.groupingBy(ImagePath::getGoodid));

        // 批量查询收藏态
        Set<Integer> favGoodsIds = new HashSet<>();
        if (userid != null) {
            List<Favorite> favList = goodsService.selectFavByUserIdAndGoodsIds(userid, goodsIds);
            favGoodsIds = favList.stream()
                    .map(Favorite::getGoodsid)
                    .collect(Collectors.toSet());
        }

        // 组装数据
        for (Goods goods : goodsList) {
            goods.setImagePaths(imageMap.getOrDefault(goods.getGoodsid(), Collections.emptyList()));
            goods.setFav(favGoodsIds.contains(goods.getGoodsid()));
        }

        return goodsList;
    }
}
