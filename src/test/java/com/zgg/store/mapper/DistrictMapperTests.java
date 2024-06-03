package com.zgg.store.mapper;

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
 * @date:2022/3/19
 */
//@SpringBootTest：标注当前的类是一个测试类，不会随同项目一块打包
@SpringBootTest
//@RunWith:表示启动这个单元测试类(单元测试类是不能够运行的)，需要传递一个参数，必须是SpringRunner实例类型
@RunWith(SpringRunner.class)
public class DistrictMapperTests {
    @Autowired
    DistrictMapper districtMapper;
    @Test
    public void findByParent(){
        List<District> byParent = districtMapper.findByParent("210100");
        for (District district : byParent) {
            System.out.println(district);
        }
    }

    @Test
    public void findNameByCode(){
        String nameByCode = districtMapper.findNameByCode("610000");
        System.out.println(nameByCode);
    }
}
