package com.zgg.store.service.impl;

import com.zgg.store.entity.Address;
import com.zgg.store.mapper.AddressMapper;
import com.zgg.store.service.AddressService;
import com.zgg.store.service.DistrictService;
import com.zgg.store.service.ex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private DistrictService districtService;

    @Value("${user.address.max-count}")
    private Integer maxCount;

    @Override
    public void addNewAddress(Integer uid, String username, Address address) {
        //判断用户的收货地址是否达到上限
        Integer count = addressMapper.countByUid(uid);
        if (count>maxCount){
            throw new AddressCountLimitException("用户的收货地址达到上限");
        }

        //堆address对象中的数据进行补全：省市区
        String provinceName = districtService.getNameByCode(address.getProvinceCode());
        String cityName = districtService.getNameByCode(address.getCityCode());
        String areaName = districtService.getNameByCode(address.getAreaCode());
        address.setProvinceName(provinceName);
        address.setCityName(cityName);
        address.setAreaName(areaName);


        //uid、isDefault
        address.setUid(uid);
        Integer isDefault = count == 0 ? 1 : 0;//1表示默认，0表示不是默认
        address.setIsDefault(isDefault);
        //补全4项日志
        address.setCreatedUser(username);
        address.setModifiedUser(username);
        address.setCreatedTime(new Date());
        address.setModifiedTime(new Date());

        Integer rows = addressMapper.insert(address);
        if(rows!=1){
            throw new InsertException("插入用户的收货地址产生未知异常");
        }

    }

    @Override
    public List<Address> getByUid(Integer uid) {
        List<Address> list = addressMapper.findByUid(uid);
        for (Address address : list) {
            //address.setAid(null);
            //address.setUid(null);
            address.setProvinceCode(null);
            address.setCityCode(null);
            address.setAreaCode(null);
            address.setTel(null);
            address.setIsDefault(null);
            address.setCreatedTime(null);
            address.setCreatedUser(null);
            address.setModifiedTime(null);
            address.setModifiedUser(null);
        }
        return list;
    }

    @Override
    public void setDefault(Integer aid, Integer uid, String username) {
        Address result = addressMapper.findByAid(aid);
        if(result==null){
            throw new AddressNotFountException("收货地址不存在");
        }
        //判断当前获取到的收货地址数据的归属
        if(!uid.equals(result.getUid())){
            throw new AccessDeniedException("非法数据访问");
        }
        //将所有地址设置为非默认
        Integer rows = addressMapper.UpdateNonDefault(uid);
        if(rows<1){
            throw new UpdateException("更新数据时产生未知异常");
        }
        //将用户选中某条地址设置为默认收货地址
        Integer integer = addressMapper.updateDefaultByAid(aid, username, new Date());
        if(integer!=1){
            throw new UpdateException("更新数据时产生未知异常");
        }

    }

    @Override
    public void delete(Integer aid, Integer uid, String username) {
        Address result = addressMapper.findByAid(aid);
        if(result==null){
            throw new AddressNotFountException("收货地址不存在");
        }
        //判断当前获取到的收货地址数据的归属
        if(!uid.equals(result.getUid())){
            throw new AccessDeniedException("非法数据访问");
        }
        Integer rows = addressMapper.deleteByAid(aid);
        if(rows!=1){
            throw new DeleteException("删除数据产生异常");
        }
        Integer count = addressMapper.countByUid(uid);
        if(count==0){
            return;
        }
        Address address = addressMapper.findLastModified(uid);
        if(result.getIsDefault()==1){
            Integer row = addressMapper.updateDefaultByAid(address.getAid(), username, new Date());
            if(row!=1){
                throw new UpdateException("更新数据时产生未知异常");
            }
        }
    }

    @Override
    public Address getByAid(Integer aid,Integer uid) {
        Address address = addressMapper.findByAid(aid);
        if(address==null){
            throw new AddressNotFountException("收货地址数据不存在");
        }
        if(!address.getUid().equals(uid)){
            throw new AccessDeniedException("发发数据访问");
        }
        address.setProvinceCode(null);
        address.setCityCode(null);
        address.setAreaCode(null);
        address.setModifiedUser(null);
        address.setCreatedUser(null);
        address.setModifiedTime(null);
        address.setCreatedTime(null);
        return address;
    }
}
