package com.zgg.store.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class District extends BaseEntity{
    private Integer id;//省市区的id
    private String parent;//省市区的父代号
    private String code;//省市区的代号
    private String name;//省市区的名字
}
