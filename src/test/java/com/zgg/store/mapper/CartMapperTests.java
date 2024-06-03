package com.zgg.store.mapper;

import com.zgg.store.entity.Cart;
import com.zgg.store.vo.CartVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
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
public class CartMapperTests {
    @Autowired
    CartMapper cartMapper;
    @Test
    public void insert(){
        Cart cart = new Cart();
        cart.setUid(10);
        cart.setPid(10000011);
        cart.setPrice(10l);
        cart.setNum(2);
        cartMapper.insert(cart);
    }
    @Test
    public void updateNumByCid(){
        cartMapper.updateNumByCid(1,4,"管理员",new Date());
    }
    @Test
    public void findByUidAndPid(){
        Cart byUidAndPid = cartMapper.findByUidAndPid(10, 10000011);
        System.out.println(byUidAndPid);
    }
    @Test
    public void findVOByUid(){
        List<CartVo> voByUid = cartMapper.findVOByUid(10);
        for (CartVo cartVo : voByUid) {
            System.out.println(cartVo);
        }
    }

    @Test
    public void findByCid(){
        Cart byCid = cartMapper.findByCid(2);
        System.out.println(byCid);
    }

    @Test
    public void findVOByCid(){
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        System.out.println(cartMapper.findVOByCid(integers));
    }

    @Test
    public void deleteByCid(){
        cartMapper.deleteByCid(3);
    }
}
