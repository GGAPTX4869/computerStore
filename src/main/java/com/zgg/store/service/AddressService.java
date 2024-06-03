package com.zgg.store.service;

import com.zgg.store.entity.Address;

import java.util.List;


public interface AddressService {
    /**
     * 添加新的收货地址
     * @param uid   用户的id
     * @param username  用户的名称
     * @param address   表单传来的数据
     */
    void addNewAddress(Integer uid, String username, Address address);

    List<Address> getByUid(Integer uid);

    /**
     * 修改某个用户的某条收货地址数据为默认收货地址
     * @param aid   收货地址的id
     * @param uid   用户的id
     * @param username  表示修改执行人
     */
    void setDefault(Integer aid,Integer uid,String username);

    /**
     * 删除用户选中的收货地址数据
     * @param aid   收货地址id
     * @param uid   用户id
     * @param username  用户名
     */
    void delete(Integer aid,Integer uid,String username);

    Address getByAid(Integer aid,Integer uid);
}
