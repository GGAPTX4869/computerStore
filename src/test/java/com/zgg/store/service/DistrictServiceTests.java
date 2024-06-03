package com.zgg.store.service;

import com.zgg.store.entity.District;
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
 * @date:2022/3/16
 */
//@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
@SpringBootTest
//@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
@RunWith(SpringRunner.class)
public class DistrictServiceTests {
    @Autowired
    DistrictService districtService;

    @Test
    public void addNewAddress(){
        //86表示钟哥，所有省的代号都是86
        List<District> byParent = districtService.getByParent("86");
        for (District district : byParent) {
            System.out.println(district);
        }
    }
}
