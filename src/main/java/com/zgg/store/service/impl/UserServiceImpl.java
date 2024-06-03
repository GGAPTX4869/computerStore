package com.zgg.store.service.impl;

import com.zgg.store.entity.User;
import com.zgg.store.mapper.UserMapper;
import com.zgg.store.service.UserService;
import com.zgg.store.service.ex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    //定义一个md5算法的加密处理
    private String getMD5Password(String password,String salt){
        //md5加密算法方法的调用
        for (int i = 0; i < 3; i++) {
            password=DigestUtils.md5DigestAsHex((salt+password+salt).getBytes()).toUpperCase();
        }
        //返回加密之后的密码
        return password;
    }

    @Override
    public void reg(User user) {
        //调用findByUsername判断用户是否注册过
        User result = userMapper.findByUsername(user.getUsername());
        if(result!=null){
            //抛出异常
            throw new UsernameDuplicatedException("用户名被占用");
        }
        //密码加密处理的实现：md5算法的形式
        //（串+password+串）
        String oldPassword = user.getPassword();
        String salt = UUID.randomUUID().toString().toUpperCase();
        //补全数据：盐值记录
        user.setSalt(salt);
        String md5Password = getMD5Password(oldPassword, salt);

        user.setPassword(md5Password);

        //补全数据：is_delete设置为0，四个日志字段信息
        user.setIsDelete(0);
        user.setCreatedUser(user.getUsername());
        user.setModifiedUser(user.getUsername());
        Date date = new Date();
        user.setCreatedTime(date);
        user.setModifiedTime(date);
        //执行注册
        Integer rows = userMapper.insert(user);
        if(rows !=1){
            throw new InsertException("在用户注册过程中产生了未知的异常");
        }

    }

    @Override
    public User login(String username, String password) {
        User result = userMapper.findByUsername(username);
        if(result == null){
            throw new UserNotFoundException("用户不存在");
        }
        //检测用户的密码是否匹配
        //获取数据库中的加密后的密码
        String oldPassword = result.getPassword();
        //获取盐值，按照相同的md5算法规则进行加密
        String salt = result.getSalt();
        String newMd5Password = getMD5Password(password, salt);
        //将密码进行比较
        if(!newMd5Password.equals(oldPassword)){
            throw new PasswordNotMatchException("用户密码错误");
        }
        //判断is_delete字段的值是否为1，表示为被标记为删除
        if(result.getIsDelete()==1){
            throw new UserNotFoundException("用户数据不存在");
        }
        //压缩数据的存储，只返回需要的数据给前端
        User user = new User();
        user.setUid(result.getUid());
        user.setUsername(result.getUsername());
        user.setAvatar(result.getAvatar());
        return user;
    }

    @Override
    public void changePassword(Integer uid, String username, String oldPassword, String newPassword) {
        User result = userMapper.findByUid(uid);
        if(result==null||result.getIsDelete()==1){
            throw new UserNotFoundException("用户数据不存在");
        }
        //输入的原始密码和数据库中的密码进行比较
        String oldMD5Password = getMD5Password(oldPassword, result.getSalt());
        if(!result.getPassword().equals(oldMD5Password)){
            throw new PasswordNotMatchException("密码错误");
        }
        //将新密码设置到数据库中
        String newMD5Password = getMD5Password(newPassword, result.getSalt());
        Integer rows = userMapper.updatePasswordByUid(uid, newMD5Password, username, new Date());
        if(rows!=1){
            throw new UpdateException("更新数据产生未知的异常");
        }
    }

    @Override
    public User getByUid(Integer uid) {
        User result = userMapper.findByUid(uid);
        if(result==null||result.getIsDelete()==1){
            throw new UserNotFoundException("用户数据不存在");
        }

        User user = new User();
        user.setUsername(result.getUsername());
        user.setPhone(result.getPhone());
        user.setEmail(result.getEmail());
        user.setGender(result.getGender());

        return user;
    }

    @Override
    public void changeInfo(Integer uid, String username, User user) {
        User result = userMapper.findByUid(uid);
        if(result==null||result.getIsDelete()==1){
            throw new UserNotFoundException("用户数据不存在");
        }
        user.setUid(uid);
        user.setModifiedUser(username);
        user.setModifiedTime(new Date());
        Integer rows = userMapper.updateInfoByUid(user);
        if(rows!=1){
            throw new UpdateException("更新数据时产生未知的异常");
        }

    }

    @Override
    public void changeAvatar(Integer uid, String avatar, String username) {
        User result = userMapper.findByUid(uid);
        if(result==null||result.getIsDelete()==1){
            throw new UserNotFoundException("用户数据不存在");
        }
        Integer rows = userMapper.updateAvatarByUid(uid, avatar, username, new Date());
        if(rows!=1){
            throw new UpdateException("更新数据时产生未知的异常");
        }
    }
}

