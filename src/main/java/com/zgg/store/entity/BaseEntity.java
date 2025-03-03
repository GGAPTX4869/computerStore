package com.zgg.store.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

//作为实体类的基类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity implements Serializable {
    private String createdUser;//日志-创建人
    private Date createdTime;//日志-创建时间
    private String modifiedUser;//日志-最后修改执行人
    private Date modifiedTime;//日志-最后修改时间
}
