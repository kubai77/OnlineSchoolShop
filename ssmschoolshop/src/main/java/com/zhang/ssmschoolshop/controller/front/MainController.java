package com.zhang.ssmschoolshop.controller.front;


import com.zhang.ssmschoolshop.entity.*;
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
            return new ArrayList<>();
        }

        List<Integer> goodsIds = new ArrayList<>();
        for (Goods goods : goodsList) {
            goodsIds.add(goods.getGoodsid());
        }

        // Batch query images
        ImagePathExample imagePathExample = new ImagePathExample();
        imagePathExample.or().andGoodidIn(goodsIds);
        List<ImagePath> allImages = goodsService.selectImagePathByExample(imagePathExample);
        Map<Integer, List<ImagePath>> imageMap = new HashMap<>();
        for (ImagePath img : allImages) {
            imageMap.computeIfAbsent(img.getGoodid(), k -> new ArrayList<>()).add(img);
        }

        // Batch query favorites
        Set<Integer> favGoodsIds = new HashSet<>();
        if (userid != null) {
            FavoriteExample favExample = new FavoriteExample();
            favExample.or().andUseridEqualTo(userid).andGoodsidIn(goodsIds);
            List<Favorite> favList = goodsService.selectFavByExample(favExample);
            for (Favorite fav : favList) {
                favGoodsIds.add(fav.getGoodsid());
            }
        }

        List<Goods> goodsAndImage = new ArrayList<>();
        //获取每个商品的图片
        for (Goods goods:goodsList) {
            goods.setFav(favGoodsIds.contains(goods.getGoodsid()));
            goods.setImagePaths(imageMap.getOrDefault(goods.getGoodsid(), new ArrayList<>()));
            goodsAndImage.add(goods);
        }
        return goodsAndImage;
    }
}
