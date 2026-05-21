package com.zhang.ssmschoolshop.service.impl;


import com.zhang.ssmschoolshop.dao.AddressMapper;
import com.zhang.ssmschoolshop.dao.OrderItemMapper;
import com.zhang.ssmschoolshop.dao.OrderMapper;
import com.zhang.ssmschoolshop.entity.*;
import com.zhang.ssmschoolshop.service.GoodsService;
import com.zhang.ssmschoolshop.service.OrderService;
import com.zhang.ssmschoolshop.service.ShopCartService;
import com.zhang.ssmschoolshop.util.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired(required = false)
    private OrderMapper orderMapper;

    @Autowired(required = false)
    private OrderItemMapper orderItemMapper;

    @Autowired(required = false)
    private AddressMapper addressMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShopCartService shopCartService;

    @Override
    public void insertOrder(Order order) {
        orderMapper.insertSelective(order);
    }

    @Override
    public void deleteById(Integer orderid) {
        orderMapper.deleteByPrimaryKey(orderid);
    }


    @Override
    public List<Order> selectOrderByExample(OrderExample orderExample) {
        return orderMapper.selectByExample(orderExample);
    }

    @Override
    public List<OrderItem> getOrderItemByExample(OrderItemExample orderItemExample) {
        return orderItemMapper.selectByExample(orderItemExample);
    }

    @Override
    public Address getAddressByKey(Integer addressid) {
        return addressMapper.selectByPrimaryKey(addressid);
    }

    @Override
    public void updateOrderByKey(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public Order selectByPrimaryKey(Integer orderid) {
        return orderMapper.selectByPrimaryKey(orderid);
    }

    @Override
    public void insertOrderItem(OrderItem orderItem) {
        orderItemMapper.insertSelective(orderItem);
    }

    @Override
    @Transactional
    public Msg processOrder(Integer userId, Float oldPrice, Float newPrice, Boolean isPay, Integer addressid) {
        // fetch cart items
        ShopCartExample shopCartExample = new ShopCartExample();
        shopCartExample.or().andUseridEqualTo(userId);
        List<ShopCart> shopCart = shopCartService.selectByExample(shopCartExample);

        if (shopCart == null || shopCart.isEmpty()) {
            return Msg.fail("购物车为空");
        }

        // check stock for each item
        for (ShopCart cart : shopCart) {
            Goods goods = goodsService.selectById(cart.getGoodsid());
            if (goods == null || goods.getNum() < cart.getGoodsnum()) {
                return Msg.fail("商品【" + (goods == null ? cart.getGoodsid() : goods.getGoodsname()) + "】库存不足");
            }
        }

        // insert order
        Order order = new Order(null, userId, new Date(), oldPrice, newPrice, isPay, false, false, false, addressid, null, null);
        orderMapper.insertSelective(order);
        Integer orderId = order.getOrderid();

        // deduct stock, insert order items, clear cart
        for (ShopCart cart : shopCart) {
            goodsService.reduceStock(cart.getGoodsid(), cart.getGoodsnum());
            orderItemMapper.insertSelective(new OrderItem(null, orderId, cart.getGoodsid(), cart.getGoodsnum()));
            shopCartService.deleteByKey(new ShopCartKey(cart.getUserid(), cart.getGoodsid()));
        }

        return Msg.success("购买成功");
    }
}
