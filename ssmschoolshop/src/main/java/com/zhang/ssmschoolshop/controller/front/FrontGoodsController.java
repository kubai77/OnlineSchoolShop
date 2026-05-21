package com.zhang.ssmschoolshop.controller.front;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhang.ssmschoolshop.entity.*;
import com.zhang.ssmschoolshop.service.*;
import com.zhang.ssmschoolshop.util.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class FrontGoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CateService cateService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @RequestMapping(value = "/detail",method = RequestMethod.GET)
    public String detailGoods(Integer goodsid, Model model, HttpSession session) {

        if(goodsid == null) {
            return "redirect:/main";
        }

        User user = (User) session.getAttribute("user");

        Map<String,Object> goodsInfo = new HashMap<String,Object>();
        Goods goods = goodsService.selectById(goodsid);

        if (user == null) {
            goods.setFav(false);
        } else {
            Favorite favorite = goodsService.selectFavByKey(new FavoriteKey(user.getUserid(), goodsid));
            if (favorite == null) {
                goods.setFav(false);
            } else {
                goods.setFav(true);
            }
        }

        Category category = cateService.selectById(goods.getCategory());
        List<ImagePath> imagePath = goodsService.findImagePath(goodsid);
        Activity activity = activityService.selectByKey(goods.getActivityid());
        goods.setActivity(activity);

        goodsInfo.put("goods", goods);
        goodsInfo.put("cate", category);
        goodsInfo.put("image", imagePath);
        model.addAttribute("goodsInfo",goodsInfo);
        model.addAttribute("commentList", getCommentList(goods.getGoodsid()));

        return "detail";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchGoods(@RequestParam(value = "page",defaultValue = "1") Integer pn, String keyword, Model model, HttpSession session) {
        PageHelper.startPage(pn, 16);

        GoodsExample goodsExample = new GoodsExample();
        goodsExample.or().andGoodsnameLike("%" + keyword + "%");
        List<Goods> goodsList = goodsService.selectByExample(goodsExample);
        goodsService.enrichGoodsList(goodsList, getUserid(session));

        PageInfo page = new PageInfo(goodsList,5);
        model.addAttribute("pageInfo", page);
        model.addAttribute("keyword", keyword);

        return "search";
    }

    @RequestMapping("/collect")
    @ResponseBody
    public Msg collectGoods(Integer goodsid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return Msg.fail("收藏失败");
        }

        Favorite favorite = new Favorite();
        favorite.setCollecttime(new Date());
        favorite.setGoodsid(goodsid);
        favorite.setUserid(user.getUserid());

        goodsService.addFavorite(favorite);

        return Msg.success("收藏成功");
    }

    @RequestMapping("/deleteCollect")
    @ResponseBody
    public Msg deleteFavGoods(Integer goodsid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Msg.fail("取消收藏失败");
        }

        goodsService.deleteFavByKey(new FavoriteKey(user.getUserid(),goodsid));

        return Msg.success("取消收藏成功");
    }

    @RequestMapping("/category")
    public String getCateGoods(String cate, @RequestParam(value = "page",defaultValue = "1") Integer pn, Model model, HttpSession session) {
        PageHelper.startPage(pn, 16);

        CategoryExample categoryExample = new CategoryExample();
        categoryExample.or().andCatenameLike(cate);
        List<Category> categoryList = cateService.selectByExample(categoryExample);

        List<Integer> cateId = new ArrayList<Integer>();
        for (Category category : categoryList) {
            cateId.add(category.getCateid());
        }

        GoodsExample goodsExample = new GoodsExample();
        goodsExample.or().andDetailcateLike("%" + cate + "%");
        if (!cateId.isEmpty()) {
            goodsExample.or().andCategoryIn(cateId);
        }
        List<Goods> goodsList = goodsService.selectByExample(goodsExample);
        goodsService.enrichGoodsList(goodsList, getUserid(session));

        PageInfo page = new PageInfo(goodsList,5);
        model.addAttribute("pageInfo", page);
        model.addAttribute("cate", cate);
        return "category";
    }



    @RequestMapping("/comment")
    @ResponseBody
    public Msg comment(Comment comment, HttpServletRequest request){
        HttpSession session=request.getSession();
        User user=(User) session.getAttribute("user");
        if (user == null) {
            return Msg.fail("评论失败");
        }
        comment.setUserid(user.getUserid());
        Date date=new Date();
        comment.setCommenttime(date);
        commentService.insertSelective(comment);
        return Msg.success("评论成功");
    }

    private Integer getUserid(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return null;
        }
        return user.getUserid();
    }

    private List<Comment> getCommentList(Integer goodsid) {
        CommentExample commentExample = new CommentExample();
        commentExample.or().andGoodsidEqualTo(goodsid);
        List<Comment> commentList = commentService.selectByExample(commentExample);
        if (commentList.isEmpty()) {
            return commentList;
        }

        Set<Integer> userIdSet = new LinkedHashSet<Integer>();
        for (Comment comment : commentList) {
            if (comment.getUserid() != null) {
                userIdSet.add(comment.getUserid());
            }
        }

        if (userIdSet.isEmpty()) {
            return commentList;
        }

        UserExample userExample = new UserExample();
        userExample.or().andUseridIn(new ArrayList<Integer>(userIdSet));
        List<User> userList = userService.selectByExample(userExample);
        Map<Integer, String> usernameMap = new HashMap<Integer, String>();
        for (User currentUser : userList) {
            usernameMap.put(currentUser.getUserid(), currentUser.getUsername());
        }

        for (Comment comment : commentList) {
            comment.setUserName(usernameMap.get(comment.getUserid()));
        }
        return commentList;
    }
}
