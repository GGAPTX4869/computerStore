package com.zgg.store.mapper;

import com.zgg.store.entity.Address;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface AddressMapper {
    /**
     * 插入用户的收货地址数据
     * @param address   收货地址数据
     * @return  受影响的行数
     */
    Integer insert(Address address);

    /**
     * 根据用户的id统计收货地址数量
     * @param uid 用户的id
     * @return  当前用户的收货地址总数
     */
    Integer countByUid(@Param("uid") Integer uid);

    /**
     * 根据用户的uid查询用户的收货地址数据
     * @param uid   用户的id
     * @return  收货地址数据
     */
    List<Address> findByUid(Integer uid);

    /**
     * 根据aid查询收货地址数据
     * @param aid   收货地址aid
     * @return  收货地址数据
     */
    Address findByAid(Integer aid);

    /**
     * 根据用户的uid值来修改用户的收货地址设置为非默认
     * @param uid   用户的id值
     * @return  受影响的行数
     */
    Integer UpdateNonDefault(Integer uid);


    /**
     *根据用户选中的记录将这条记录射为默认收货地址
     * @param aid
     * @param modifiedUser
     * @param modifiedTime
     * @return
     */
    Integer updateDefaultByAid(@Param("aid") Integer aid,@Param("modifiedUser") String modifiedUser,@Param("modifiedTime") Date modifiedTime);

    /**
     * 根据收货地址id删除收货地址数据
     * @param aid   收货地址的id
     * @return  受影响的行数
     */
    Integer deleteByAid(Integer aid);

    /**
     * 根据用户uid查询当前用户最后一次被修改的收货地址数据
     * @param uid   用户id
     * @return 收货地址数据
     */
    Address findLastModified(Integer uid);
}
