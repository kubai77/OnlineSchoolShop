package com.zhang.ssmschoolshop.service.impl;


import com.zhang.ssmschoolshop.dao.AddressMapper;
import com.zhang.ssmschoolshop.dao.OrderItemMapper;
import com.zhang.ssmschoolshop.dao.OrderMapper;
import com.zhang.ssmschoolshop.entity.*;
import com.zhang.ssmschoolshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhang.ssmschoolshop.dao.ShopCartMapper;
import com.zhang.ssmschoolshop.dao.GoodsMapper;
import org.springframework.transaction.annotation.Transactional;

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
    private GoodsMapper goodsMapper;

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
    @Transactional(rollbackFor = Exception.class)
    public void placeOrder(List<ShopCart> shopCartList, Order order) {
        // 1. 检查库存并扣减
        for (ShopCart cart : shopCartList) {
            Goods goods = goodsMapper.selectByPrimaryKey(cart.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在");
            }
            if (goods.getNum() < cart.getGoodsnum()) {
                throw new RuntimeException("商品 " + goods.getGoodsname() + " 库存不足");
            }
            // 扣减库存
            goods.setNum(goods.getNum() - cart.getGoodsnum());
            goodsMapper.updateByPrimaryKeySelective(goods);
            
            // 清理购物车
            shopCartMapper.deleteByPrimaryKey(new ShopCartKey(cart.getUserid(), cart.getGoodsid()));
        }

        // 2. 插入订单
        orderMapper.insertSelective(order);
        Integer orderId = order.getOrderid();

        // 3. 插入订单项
        for (ShopCart cart : shopCartList) {
            orderItemMapper.insertSelective(new OrderItem(null, orderId, cart.getGoodsid(), cart.getGoodsnum()));
        }
    }
}
