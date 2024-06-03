package com.zgg.store.service;

import com.zgg.store.entity.Address;
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
public class AddressServiceTests {
    @Autowired
    AddressService addressService;

    @Test
    public void addNewAddress(){
        Address address = new Address();
        address.setName("12345679");
        address.setPhone("987654321");
        addressService.addNewAddress(3,"管理员",address);
    }

    @Test
    public void getByUid(){
        List<Address> byUid = addressService.getByUid(10);
        for (Address address : byUid) {
            System.out.println(address);
        }
    }
    @Test
    public void setDefault(){
        addressService.setDefault(7,10,"管理员");
    }
    @Test
    public void delete(){
        addressService.delete(5,10,"管理员");
    }
}
