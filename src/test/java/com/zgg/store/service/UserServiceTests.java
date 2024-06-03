package com.zgg.store.service;

import com.zgg.store.entity.User;
import com.zgg.store.service.ex.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
public class UserServiceTests {
    @Autowired
    UserService userService;

    @Test
    public void reg(){
        try {
            User user = new User();
            user.setUsername("lzj");
            user.setPassword("123");
            userService.reg(user);
        }catch (ServiceException e){
            System.out.println(e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void login(){
        User lzj = userService.login("lzj", "123");
        System.out.println(lzj);
    }

    @Test
    public void changePassword(){
        userService.changePassword(10,"lzj","1234","123");
    }

    @Test
    public void getByUid(){
        User byUid = userService.getByUid(10);
        System.out.println(byUid);
    }
    @Test
    public void changeInfo(){
        User user = new User();
        user.setPhone("1123456");
        user.setEmail("21221212@qq.com");
        user.setGender(0);
        userService.changeInfo(10,"lzj",user);
    }

    @Test
    public void changeAvatar(){
        userService.changeAvatar(10,"qwqreqwr","管理员");
    }

}
