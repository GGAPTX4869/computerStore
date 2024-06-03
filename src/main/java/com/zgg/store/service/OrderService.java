package com.zgg.store.service;

import com.zgg.store.entity.Order;
import com.zgg.store.vo.OrderItemVo;

import java.util.List;


public interface OrderService {
    Order create(Integer aid, Integer uid, String username, List<Integer> cids);
    List<OrderItemVo> selectVoOrderItem(Integer uid);

}
