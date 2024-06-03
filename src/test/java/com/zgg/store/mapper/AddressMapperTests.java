package com.zgg.store.mapper;

import com.zgg.store.entity.Address;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
public class AddressMapperTests {
    @Autowired
    AddressMapper addressMapper;
    @Test
    public void insert(){
        Address address = new Address();
        address.setUid(2);
        address.setName("ewrtertert");
        address.setPhone("5354asd435");
        addressMapper.insert(address);
    }
    @Test
    public void countByUid(){

        System.out.println(addressMapper.countByUid(2));
    }

    @Test
    public void findByUid(){
        List<Address> byUid = addressMapper.findByUid(10);
        for (Address address : byUid) {
            System.out.println(address);
        }
    }
    @Test
    public void findByAid(){

        System.out.println(addressMapper.findByAid(6));
    }
    @Test
    public void UpdateNonDefault(){

        addressMapper.UpdateNonDefault(10);
    }
    @Test
    public void updateDefaultByAid(){

        addressMapper.updateDefaultByAid(6,"管理员",new Date());
    }
    @Test
    public void deleteByAid(){
        addressMapper.deleteByAid(4);
    }
    @Test
    public void findLastModified(){
        Address lastModified = addressMapper.findLastModified(10);
        System.out.println(lastModified);
    }
}
