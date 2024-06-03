package com.zgg.store.controller;

import com.zgg.store.entity.Order;
import com.zgg.store.service.OrderService;
import com.zgg.store.util.JsonResult;
import com.zgg.store.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @autor:zgg
 * @date:2023/13/23
 */
@RestController
@RequestMapping("/orders")
public class OrderController extends BaseController{
    @Autowired
    private OrderService orderService;

    @RequestMapping("/create")
    public JsonResult<Order> create(Integer aid, @RequestParam List<Integer> cids, HttpSession session){
        Order data = orderService.create(aid, getuidFromSession(session), getUsernameFromSession(session), cids);
        return new JsonResult<>(OK,data);
    }

    @RequestMapping("/list")
    public JsonResult<List<OrderItemVo>> selectVoOrderItem( HttpSession session){
        List<OrderItemVo> data = orderService.selectVoOrderItem(getuidFromSession(session));
        return new JsonResult<>(OK,data);
    }
}
