package com.zgg.store.mapper;

import com.zgg.store.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface UserMapper {
    /**
     * 插入用户的数据
     * @param user 用户的数据
     * @return 受影响的行数
     */
    Integer insert(User user);

    /**
     * 根据用户名来查询用户的数据
     * @param username 用户名
     * @return 如果找到对应的用户则返回这个用户丶数据，如果没有找到则返回null
     */
    User findByUsername(String username);

    /**
     * 根据用户的uid来修改用户密码
     * @param uid 用户的uid
     * @param password 用户输入的新密码
     * @param modifiedUser 表示修改的执行者
     * @param modifiedTime 表示修改数据的时间
     * @return 返回值为受影响的行数
     */
    Integer updatePasswordByUid(@Param("uid") Integer uid,@Param("password") String password,@Param("modifiedUser") String modifiedUser,@Param("modifiedTime") Date modifiedTime);

    /**
     * 根据uid查询用户的数据
     * @param uid 用户的id
     * @return 返回为查询到的数据
     */
    User findByUid(Integer uid);

    /**
     * 更新用户的数据信息
     * @param user  用户的数据
     * @return 返回值为受影响的行数
     */
    Integer updateInfoByUid(User user);

    /**
     * 上传头像路径
     * @param uid 用户的uid
     * @param avatar    用户上柴内动路径
     * @param modifiedUser  修改者
     * @param modifiedTime  修改时间
     * @return
     */
    Integer updateAvatarByUid(@Param("uid") Integer uid,@Param("avatar") String avatar,@Param("modifiedUser") String modifiedUser,@Param("modifiedTime") Date modifiedTime);
}
