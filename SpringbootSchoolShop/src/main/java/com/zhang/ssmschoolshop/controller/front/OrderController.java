package com.zhang.ssmschoolshop.controller.front;

import com.zhang.ssmschoolshop.entity.*;
import com.zhang.ssmschoolshop.service.ActivityService;
import com.zhang.ssmschoolshop.service.AddressService;
import com.zhang.ssmschoolshop.service.EmailService;
import com.zhang.ssmschoolshop.service.GoodsService;
import com.zhang.ssmschoolshop.service.OrderService;
import com.zhang.ssmschoolshop.service.ShopCartService;
import com.zhang.ssmschoolshop.util.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

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
    private EmailService emailService;

    @Value("${mail.notification.order-to-admin-enabled:false}")
    private boolean orderToAdminEmailEnabled;

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

            Activity activity = activityService.selectByKey(goods.getActivityid());
            goods.setActivity(activity);

            if (activity.getDiscount() != 1) {
                goods.setNewPrice(goods.getPrice() * goods.getNum() * activity.getDiscount());
                System.out.println("价格为：" + goods.getPrice() * goods.getNum() * activity.getDiscount());
            } else if (activity.getFullnum() != null) {
                System.out.println("进入第二层方法");
                if (goods.getNum() >= activity.getFullnum()) {
                    goods.setNewPrice((float) (goods.getPrice() * (goods.getNum() - activity.getReducenum())));
                } else {
                    goods.setNewPrice((float) (goods.getPrice() * goods.getNum()));
                }
            } else if (activity.getFullprice() != null && activity.getReducenum() != null) {
                if ((goods.getNum() * goods.getNum()) > activity.getFullprice()) {
                    goods.setNewPrice((float) (goods.getPrice() * goods.getNum() - activity.getReducenum()));
                } else {
                    goods.setNewPrice((float) (goods.getPrice() * goods.getNum()));

                }

            } else {
                goods.setNewPrice((float) (goods.getPrice() * goods.getNum()));
            }
            totalPrice = totalPrice + goods.getNewPrice();
            oldTotalPrice = oldTotalPrice + goods.getNum() * goods.getPrice();
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

        if (orderToAdminEmailEnabled) {
            emailService.sendEmailToAdmin();
        } else {
            log.debug("订单通知邮件未启用，orderId={}", orderId);
        }
        return Msg.success("购买成功");
    }

}
