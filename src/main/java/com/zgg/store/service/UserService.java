package com.zgg.store.service;

import com.zgg.store.entity.User;


public interface UserService {
    /**
     * 用户注册方法
     * @param user 用户的数据
     */
    void reg(User user);

    /**
     * 用户登录方法
     * @param username 用户名
     * @param password 用户的密码
     * @return 当前匹配的用户数据，如果没有则返回null
     */
    User login(String username,String password);

    /**
     * 用户更改密码方法
     * @param uid 用户的uid
     * @param username 用户名
     * @param oldPassword 用户的旧密码
     * @param newPassword 用户传过来的新密码
     */
    void changePassword(Integer uid,String username,String oldPassword,String newPassword);

    /**
     * 根据uid查询用户的数据
     * @param uid 用户的id
     * @return 返回为查询到的数据
     */
    User getByUid(Integer uid);

    /**
     *更新用户的数据操作
     * @param uid   uid用户的id
     * @param username  用户的名称
     * @param user  用户对象的数据
     */
    void changeInfo(Integer uid,String username,User user);

    /**
     * 跟新用户的头像
     * @param uid    用户的uid
     * @param avatar    头像的催促路径
     * @param username  用户的名称
     */
    void changeAvatar(Integer uid,String avatar,String username);


}
