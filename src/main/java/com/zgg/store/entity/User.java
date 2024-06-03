package com.zgg.store.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

//用户的实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class User extends BaseEntity implements Serializable {
    private Integer uid;//用户id
    private String username;//用户名
    private String password;//密码
    private String salt;//盐值
    private String phone;//电话号码
    private String email;//电子邮箱
    private Integer gender;//性别:0-女，1-男
    private String avatar;//头像
    private Integer isDelete;//是否删除：0-未删除，1-已删除
}
