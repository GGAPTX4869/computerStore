package com.zgg.store.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity{
    private Integer oid;//订单id
    private Integer uid;//用户id
    private String recvName;//收货人姓名
    private String recvPhone;//收货人电话
    private String recvProvince;//收货人所在省
    private String recvCity;//收货人所在市
    private String recvArea;//收货人所在区
    private String recvAddress;//收货详细地址
    private Long totalPrice;//总价
    private Integer status;//状态：0-未支付，1-已支付，2-已取消，3-已关闭，4-已完成
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;//下单时间
    private Date payTime;//支付时间
}
