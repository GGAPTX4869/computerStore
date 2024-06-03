package com.zgg.store.mapper;

import com.zgg.store.entity.Order;
import com.zgg.store.entity.OrderItem;
import com.zgg.store.vo.OrderItemVo;

import java.util.List;


public interface OrderMapper {
    /**
     * 插入订单数据
     * @param order 订单数据
     * @return  受影响的行数
     */
    Integer insertOrder(Order order);

    /**
     * 插入订单项数据
     * @param orderItem 订单项数据
     * @return  受影响的行数
     */
    Integer insertOrderItem(OrderItem orderItem);

    List<OrderItemVo> selectVoOrderItem(Integer uid);

    List<Order> selectOrderByUid(Integer uid);
}
