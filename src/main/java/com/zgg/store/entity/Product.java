package com.zgg.store.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity implements Serializable {
    private Integer id;//商品id
    private Integer categoryId;//分类id
    private String itemType;//商品系列
    private String title;//商品标题
    private String sellPoint;//商品卖点
    private Long price;//商品单价
    private Integer num;//库存数量
    private String image;//图片路径
    private Integer status;//商品状态  1：上架   2：下架   3：删除
    private Integer priority;//显示优先级
}
