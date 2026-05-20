package com.zhang.ssmschoolshop.controller.front;

import com.zhang.ssmschoolshop.entity.Activity;
import com.zhang.ssmschoolshop.entity.Address;
import com.zhang.ssmschoolshop.entity.AddressExample;
import com.zhang.ssmschoolshop.entity.Goods;
import com.zhang.ssmschoolshop.entity.ImagePath;
import com.zhang.ssmschoolshop.entity.Order;
import com.zhang.ssmschoolshop.entity.OrderItem;
import com.zhang.ssmschoolshop.entity.ShopCart;
import com.zhang.ssmschoolshop.entity.ShopCartExample;
import com.zhang.ssmschoolshop.entity.ShopCartKey;
import com.zhang.ssmschoolshop.entity.User;
import com.zhang.ssmschoolshop.service.ActivityService;
import com.zhang.ssmschoolshop.service.AddressService;
import com.zhang.ssmschoolshop.service.EmailService;
import com.zhang.ssmschoolshop.service.GoodsService;
import com.zhang.ssmschoolshop.service.OrderPriceCalculator;
import com.zhang.ssmschoolshop.service.OrderService;
import com.zhang.ssmschoolshop.service.ShopCartService;
import com.zhang.ssmschoolshop.util.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private ShopCartService shopCartService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private OrderPriceCalculator orderPriceCalculator;

    @Autowired
    private EmailService emailService;

    @RequestMapping("/order")
    public String showOrder(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        AddressExample addressExample = new AddressExample();
        addressExample.or().andUseridEqualTo(user.getUserid());
        List<Address> addressList = addressService.getAllAddressByExample(addressExample);

        model.addAttribute("address", addressList);

        ShopCartExample shopCartExample = new ShopCartExample();
        shopCartExample.or().andUseridEqualTo(user.getUserid());
        List<ShopCart> shopCart = shopCartService.selectByExample(shopCartExample);

        List<Goods> goodsAndImage = new ArrayList<>();

        Float totalPrice = 0F;
        Integer oldTotalPrice = 0;

        for (ShopCart cart : shopCart) {
            Goods goods = goodsService.selectById(cart.getGoodsid());

            List<ImagePath> imagePathList = goodsService.findImagePath(goods.getGoodsid());
            goods.setImagePaths(imagePathList);
            goods.setNum(cart.getGoodsnum());

            Activity activity = null;
            if (goods.getActivityid() != null) {
                activity = activityService.selectByKey(goods.getActivityid());
            }
            goods.setActivity(activity);
            goods.setNewPrice(orderPriceCalculator.calculateNewPrice(goods, activity));

            totalPrice += goods.getNewPrice();
            oldTotalPrice += goods.getNum() * goods.getPrice();
            goodsAndImage.add(goods);
        }

        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("oldTotalPrice", oldTotalPrice);
        model.addAttribute("goodsAndImage", goodsAndImage);

        return "orderConfirm";
    }

    @RequestMapping("/orderFinish")
    @ResponseBody
    public Msg orderFinish(Float oldPrice, Float newPrice, Boolean isPay, Integer addressid, HttpSession session) {

        User user = (User) session.getAttribute("user");

        ShopCartExample shopCartExample = new ShopCartExample();
        shopCartExample.or().andUseridEqualTo(user.getUserid());
        List<ShopCart> shopCart = shopCartService.selectByExample(shopCartExample);

        for (ShopCart cart : shopCart) {
            shopCartService.deleteByKey(new ShopCartKey(cart.getUserid(), cart.getGoodsid()));
        }

        Order order = new Order(null, user.getUserid(), new Date(), oldPrice, newPrice, isPay, false, false, false, addressid, null, null);
        orderService.insertOrder(order);
        Integer orderId = order.getOrderid();

        for (ShopCart cart : shopCart) {
            orderService.insertOrderItem(new OrderItem(null, orderId, cart.getGoodsid(), cart.getGoodsnum()));
        }
        return Msg.success("购买成功");
    }
}
