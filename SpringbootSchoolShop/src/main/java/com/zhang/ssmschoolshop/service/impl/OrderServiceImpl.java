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
    public void createOrderFromCart(Integer userid, Float oldPrice, Float newPrice, Boolean isPay, Integer addressid) {
        ShopCartExample shopCartExample = new ShopCartExample();
        shopCartExample.or().andUseridEqualTo(userid);
        List<ShopCart> shopCartList = shopCartMapper.selectByExample(shopCartExample);
        if (shopCartList == null || shopCartList.isEmpty()) {
            throw new IllegalStateException("购物车为空，无法下单");
        }

        for (ShopCart cart : shopCartList) {
            validateStock(cart);
        }

        Order order = new Order(null, userid, new Date(), oldPrice, newPrice, isPay, false, false, false, addressid, null, null);
        orderMapper.insertSelective(order);
        Integer orderId = order.getOrderid();

        for (ShopCart cart : shopCartList) {
            int updatedRows = goodsService.reduceStock(cart.getGoodsid(), cart.getGoodsnum());
            if (updatedRows == 0) {
                throw buildStockException(cart.getGoodsid());
            }
            orderItemMapper.insertSelective(new OrderItem(null, orderId, cart.getGoodsid(), cart.getGoodsnum()));
        }

        for (ShopCart cart : shopCartList) {
            shopCartMapper.deleteByPrimaryKey(new ShopCartKey(cart.getUserid(), cart.getGoodsid()));
        }
    }

    private void validateStock(ShopCart cart) {
        Goods goods = goodsService.selectById(cart.getGoodsid());
        if (goods == null) {
            throw new IllegalStateException("商品不存在，无法完成下单");
        }
        Integer stock = goods.getNum() == null ? 0 : goods.getNum();
        if (stock < cart.getGoodsnum()) {
            throw new IllegalStateException(goods.getGoodsname() + "库存不足，当前库存仅剩" + stock + "件");
        }
    }

    private IllegalStateException buildStockException(Integer goodsid) {
        Goods goods = goodsService.selectById(goodsid);
        if (goods == null) {
            return new IllegalStateException("商品不存在，无法完成下单");
        }
        Integer stock = goods.getNum() == null ? 0 : goods.getNum();
        return new IllegalStateException(goods.getGoodsname() + "库存不足，当前库存仅剩" + stock + "件");
    }
}
