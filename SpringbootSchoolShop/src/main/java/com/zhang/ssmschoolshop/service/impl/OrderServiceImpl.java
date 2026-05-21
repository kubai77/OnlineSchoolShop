package com.zhang.ssmschoolshop.service.impl;


import com.zhang.ssmschoolshop.dao.AddressMapper;
import com.zhang.ssmschoolshop.dao.OrderItemMapper;
import com.zhang.ssmschoolshop.dao.OrderMapper;
import com.zhang.ssmschoolshop.dao.ShopCartMapper;
import com.zhang.ssmschoolshop.entity.*;
import com.zhang.ssmschoolshop.service.GoodsService;
import com.zhang.ssmschoolshop.service.OrderService;
import com.zhang.ssmschoolshop.service.ShopCartService;
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
    
    @Autowired(required = false)
    private ShopCartMapper shopCartMapper;
    
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
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Integer userid, Float oldPrice, Float newPrice, Boolean isPay, Integer addressid) {
        ShopCartExample shopCartExample = new ShopCartExample();
        shopCartExample.or().andUseridEqualTo(userid);
        List<ShopCart> shopCartList = shopCartService.selectByExample(shopCartExample);
        
        if (shopCartList == null || shopCartList.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }
        
        for (ShopCart cart : shopCartList) {
            Goods goods = goodsService.selectById(cart.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在");
            }
            if (goods.getNum() == null || goods.getNum() < cart.getGoodsnum()) {
                throw new RuntimeException("商品 " + goods.getGoodsname() + " 库存不足，当前库存为 " + (goods.getNum() != null ? goods.getNum() : 0));
            }
        }
        
        Order order = new Order(null, userid, new Date(), oldPrice, newPrice, isPay, false, false, false, addressid, null, null);
        insertOrder(order);
        
        for (ShopCart cart : shopCartList) {
            OrderItem orderItem = new OrderItem(null, order.getOrderid(), cart.getGoodsid(), cart.getGoodsnum());
            insertOrderItem(orderItem);
            
            boolean success = goodsService.decreaseStock(cart.getGoodsid(), cart.getGoodsnum());
            if (!success) {
                throw new RuntimeException("扣减库存失败");
            }
        }
        
        for (ShopCart cart : shopCartList) {
            shopCartService.deleteByKey(new ShopCartKey(cart.getUserid(), cart.getGoodsid()));
        }
        
        return order;
    }
}
