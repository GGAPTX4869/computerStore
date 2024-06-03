package com.zgg.store.vo;

import com.zgg.store.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemVo {
    private Integer oid;//订单号
    private Integer uid;//用户id
    private String title;//商品标题
    private Long price;//商品价格
    private Integer num;//数量
    private String image;//图片
    private Integer status;//状态
    private List<Order> orders;//order表

}
