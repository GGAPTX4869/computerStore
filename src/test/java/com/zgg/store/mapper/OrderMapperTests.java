package com.zgg.store.mapper;

import com.zgg.store.entity.Order;
import com.zgg.store.entity.OrderItem;
import com.zgg.store.vo.OrderItemVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @autor:lzj
 * @date:2022/3/19
 */
//@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
@SpringBootTest
//@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
@RunWith(SpringRunner.class)
public class OrderMapperTests {
    @Autowired
    OrderMapper orderMapper;
    @Test
    public void insertOrder(){
        Order order = new Order();
        order.setUid(10);
        order.setRecvName("红红");
        order.setRecvPhone("111556423");
        orderMapper.insertOrder(order);
    }
    @Test
    public void insertOrderItem(){
        OrderItem orderItem = new OrderItem();
        orderItem.setOid(1);
        orderItem.setPid(10000003);
        orderItem.setTitle("广博(GuangBo)16K115页线圈记事本子日记本文具笔记本图案随机");
        orderMapper.insertOrderItem(orderItem);

    }

    @Test
    public void selectVoOrderItem(){
        ArrayList<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(4);
        List<OrderItemVo> orderItemVos = orderMapper.selectVoOrderItem(10);
        for (OrderItemVo orderItemVo : orderItemVos) {
            System.out.println(orderItemVo);
        }
    }
    @Test
    public void selectOrderOidByUid(){
        System.out.println(orderMapper.selectOrderByUid(10));
    }
}
