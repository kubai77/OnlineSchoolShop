package com.zhang.ssmschoolshop.service.impl;


import com.zhang.ssmschoolshop.dao.AddressMapper;
import com.zhang.ssmschoolshop.dao.OrderItemMapper;
import com.zhang.ssmschoolshop.dao.OrderMapper;
import com.zhang.ssmschoolshop.dao.ShopCartMapper;
import com.zhang.ssmschoolshop.entity.*;
import com.zhang.ssmschoolshop.service.GoodsService;
import com.zhang.ssmschoolshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired(required = false)
    private ShopCartMapper shopCartMapper;

    @Autowired(required = false)
    private GoodsService goodsService;

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
    public void createOrder(Integer userId, Float oldPrice, Float newPrice, Boolean isPay, Integer addressid) {
        ShopCartExample shopCartExample = new ShopCartExample();
        shopCartExample.or().andUseridEqualTo(userId);
        List<ShopCart> shopCart = shopCartMapper.selectByExample(shopCartExample);

        for (ShopCart cart : shopCart) {
            int result = goodsService.reduceStock(cart.getGoodsid(), cart.getGoodsnum());
            if (result == 0) {
                Goods goods = goodsService.selectById(cart.getGoodsid());
                String goodsName = goods != null ? goods.getGoodsname() : "ID=" + cart.getGoodsid();
                throw new RuntimeException("商品 [" + goodsName + "] 库存不足，购买失败");
            }
        }

        Order order = new Order(null, userId, new Date(), oldPrice, newPrice, isPay, false, false, false, addressid, null, null);
        orderMapper.insertSelective(order);
        Integer orderId = order.getOrderid();

        for (ShopCart cart : shopCart) {
            orderItemMapper.insertSelective(new OrderItem(null, orderId, cart.getGoodsid(), cart.getGoodsnum()));
        }

        shopCartMapper.deleteByExample(shopCartExample);
    }
}
