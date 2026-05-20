package com.zhang.ssmschoolshop.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhang.ssmschoolshop.entity.Address;
import com.zhang.ssmschoolshop.entity.Admin;
import com.zhang.ssmschoolshop.entity.Goods;
import com.zhang.ssmschoolshop.entity.Order;
import com.zhang.ssmschoolshop.entity.OrderExample;
import com.zhang.ssmschoolshop.entity.OrderItem;
import com.zhang.ssmschoolshop.entity.OrderItemExample;
import com.zhang.ssmschoolshop.service.EmailService;
import com.zhang.ssmschoolshop.service.GoodsService;
import com.zhang.ssmschoolshop.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private EmailService emailService;

    @Value("${mail.notification.shipment-to-user-enabled:false}")
    private boolean shipmentToUserEmailEnabled;

    @RequestMapping("/send")
    public String sendOrder(@RequestParam(value = "page", defaultValue = "1") Integer pn, Model model, HttpSession session) {

        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        PageHelper.startPage(pn, 2);

        OrderExample orderExample = new OrderExample();
        orderExample.or().andIssendEqualTo(false);
        List<Order> orderList = orderService.selectOrderByExample(orderExample);
        model.addAttribute("sendOrder", orderList);

        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.or().andOrderidEqualTo(order.getOrderid());
            List<OrderItem> orderItemList = orderService.getOrderItemByExample(orderItemExample);

            List<Goods> goodsList = new ArrayList<>();
            for (OrderItem orderItem : orderItemList) {
                Goods goods = goodsService.selectById(orderItem.getGoodsid());
                goods.setNum(orderItem.getNum());
                goodsList.add(goods);
            }

            order.setGoodsInfo(goodsList);

            Address address = orderService.getAddressByKey(order.getAddressid());
            order.setAddress(address);

            orderList.set(i, order);
        }

        PageInfo<Order> page = new PageInfo<>(orderList, 5);
        model.addAttribute("pageInfo", page);

        return "adminAllOrder";
    }

    @RequestMapping("/sendGoods")
    public String sendGoods(Integer orderid, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        Order order = new Order();
        order.setOrderid(orderid);
        order.setIssend(true);
        orderService.updateOrderByKey(order);
        if (shipmentToUserEmailEnabled) {
            emailService.sendEmailToUser();
        } else {
            log.debug("发货通知邮件未启用，orderId={}", orderid);
        }
        return "redirect:/admin/order/send";
    }

    @RequestMapping("/receiver")
    public String receiveOrder(@RequestParam(value = "page", defaultValue = "1") Integer pn, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        PageHelper.startPage(pn, 2);

        OrderExample orderExample = new OrderExample();
        orderExample.or().andIssendEqualTo(true).andIsreceiveEqualTo(false);
        List<Order> orderList = orderService.selectOrderByExample(orderExample);
        model.addAttribute("sendOrder", orderList);

        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.or().andOrderidEqualTo(order.getOrderid());
            List<OrderItem> orderItemList = orderService.getOrderItemByExample(orderItemExample);

            List<Goods> goodsList = new ArrayList<>();
            for (OrderItem orderItem : orderItemList) {
                Goods goods = goodsService.selectById(orderItem.getGoodsid());
                goods.setNum(orderItem.getNum());
                goodsList.add(goods);
            }
            order.setGoodsInfo(goodsList);

            Address address = orderService.getAddressByKey(order.getAddressid());
            order.setAddress(address);

            orderList.set(i, order);
        }

        PageInfo<Order> page = new PageInfo<>(orderList, 5);
        model.addAttribute("pageInfo", page);

        return "adminOrderReceive";
    }

    @RequestMapping("/complete")
    public String completeOrder(@RequestParam(value = "page", defaultValue = "1") Integer pn, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        PageHelper.startPage(pn, 2);

        OrderExample orderExample = new OrderExample();
        orderExample.or().andIssendEqualTo(true).andIsreceiveEqualTo(true).andIscompleteEqualTo(true);
        List<Order> orderList = orderService.selectOrderByExample(orderExample);
        model.addAttribute("sendOrder", orderList);

        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.or().andOrderidEqualTo(order.getOrderid());
            List<OrderItem> orderItemList = orderService.getOrderItemByExample(orderItemExample);

            List<Goods> goodsList = new ArrayList<>();
            for (OrderItem orderItem : orderItemList) {
                Goods goods = goodsService.selectById(orderItem.getGoodsid());
                goods.setNum(orderItem.getNum());
                goodsList.add(goods);
            }

            order.setGoodsInfo(goodsList);

            Address address = orderService.getAddressByKey(order.getAddressid());
            order.setAddress(address);

            orderList.set(i, order);
        }

        PageInfo<Order> page = new PageInfo<>(orderList, 5);
        model.addAttribute("pageInfo", page);
        return "adminOrderComplete";
    }
}
