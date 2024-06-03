package com.zgg.store.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartVo implements Serializable {
    private Integer cid;//购物车数据id
    private Integer uid;//用户id
    private Integer pid;//商品id
    private Long price;//加入时商品单价
    private Integer num;//商品数量
    private String title;//标题
    private String image;//图片
    private Long realPrice;//最新价格

}
