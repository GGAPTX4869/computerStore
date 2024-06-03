package com.zgg.store.mapper;

import com.zgg.store.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

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
public class UserMapperTests {
    @Autowired
    UserMapper userMapper;

    @Test
    public void insert(){
        User user = new User();
        user.setUsername("ti");
        user.setPassword("123");
        Integer insert = userMapper.insert(user);
        System.out.println(insert);
    }

    @Test
    public void findByUsername(){
        User tim = userMapper.findByUsername("lzj");
        System.out.println(tim);
    }

    @Test
    public void updatePasswordByUid(){
        userMapper.updatePasswordByUid(2,"1234","管理员",new Date());

    }
    @Test
    public void findByUid(){
        User byUid = userMapper.findByUid(10);
        System.out.println(byUid);
    }

    @Test
    public void updateInfoByUid(){
        User user = new User();
        user.setUid(10);
        user.setPhone("123456789");
        user.setEmail("123456@qq.com");
        user.setGender(1);
        userMapper.updateInfoByUid(user);
    }

    @Test
    public void updateAvatarByUid(){
        userMapper.updateAvatarByUid(10,"1324654","lzj",new Date());
    }
}
