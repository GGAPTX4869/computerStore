package com.zgg.store.service.impl;

import com.zgg.store.entity.Address;
import com.zgg.store.entity.Order;
import com.zgg.store.entity.OrderItem;
import com.zgg.store.mapper.OrderMapper;
import com.zgg.store.service.AddressService;
import com.zgg.store.service.CartService;
import com.zgg.store.service.OrderService;
import com.zgg.store.service.ex.InsertException;
import com.zgg.store.vo.CartVo;
import com.zgg.store.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CartService cartService;

    @Override
    public Order create(Integer aid, Integer uid, String username, List<Integer> cids) {
        List<CartVo> list = cartService.getVOByCid(cids, uid);
        //计算产品的总价
        Long totalPrice=0L;
        for (CartVo cartVo : list) {
            totalPrice+=cartVo.getRealPrice()*cartVo.getNum();
        }
        Address address = addressService.getByAid(aid, uid);
        Order order = new Order();
        order.setUid(uid);
        order.setRecvName(address.getName());
        order.setRecvPhone(address.getPhone());
        order.setRecvProvince(address.getProvinceName());
        order.setRecvCity(address.getCityName());
        order.setRecvArea(address.getAreaName());
        order.setRecvAddress(address.getAddress());
        //状态、总价、时间
        order.setStatus(0);
        order.setTotalPrice(totalPrice);
        order.setOrderTime(new Date());
        //日志
        order.setCreatedUser(username);
        order.setModifiedUser(username);
        order.setCreatedTime(new Date());
        order.setModifiedTime(new Date());

        Integer integer = orderMapper.insertOrder(order);
        if(integer!=1){
            throw new InsertException("插入数据异常");
        }

        //创建订单详细项的数据

        for (CartVo cartVo : list) {
            //创建一个订单项数据对象
            OrderItem orderItem = new OrderItem();
            orderItem.setOid(order.getOid());
            orderItem.setPid(cartVo.getPid());
            orderItem.setTitle(cartVo.getTitle());
            orderItem.setImage(cartVo.getImage());
            orderItem.setPrice(cartVo.getRealPrice());
            orderItem.setNum(cartVo.getNum());
            //日志
            orderItem.setCreatedUser(username);
            orderItem.setModifiedUser(username);
            orderItem.setCreatedTime(new Date());
            orderItem.setModifiedTime(new Date());
            //插入数据
            Integer integer1 = orderMapper.insertOrderItem(orderItem);
            if(integer1!=1){
                throw new InsertException("插入数据异常");
            }
        }

        return order;
    }

    @Override
    public List<OrderItemVo> selectVoOrderItem(Integer uid) {
        List<OrderItemVo> orderItemVos = orderMapper.selectVoOrderItem(uid);
        List<Order> list = orderMapper.selectOrderByUid(uid);
        for (OrderItemVo orderItemVo : orderItemVos) {
            orderItemVo.setOrders(list);
        }

        return orderItemVos;
    }
}
