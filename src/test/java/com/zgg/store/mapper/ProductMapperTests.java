package com.zgg.store.mapper;

import com.zgg.store.entity.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
public class ProductMapperTests {
    @Autowired
    ProductMapper productMapper;
    @Test
    public void findHotList(){
        List<Product> hotList = productMapper.findHotList();
        for (Product product : hotList) {
            System.out.println(product);
        }
    }
}
