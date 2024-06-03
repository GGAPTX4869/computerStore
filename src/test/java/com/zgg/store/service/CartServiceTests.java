package com.zgg.store.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * <p>
 *
 * </p>
 *
 * @autor:lzj
 * @date:2022/3/16
 */
//@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
@SpringBootTest
//@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
@RunWith(SpringRunner.class)
public class CartServiceTests {
    @Autowired
    CartService cartService;

    @Test
    public void addToCart(){
        cartService.addToCart(10,10000003,2,"管理员");
    }
    @Test
    public void addNum(){
        cartService.addNum(3,10,"管理员");
    }
    @Test
    public void getVOByCid(){
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        System.out.println(cartService.getVOByCid(integers,10));
    }
    @Test
    public void reduceNum(){
        cartService.reduceNum(3,10,"管理员");
    }
    @Test
    public void deleteByCid(){
        cartService.deleteByCid(2,10);
    }
}
