package com.zgg.store.controller;

import com.zgg.store.service.CartService;
import com.zgg.store.util.JsonResult;
import com.zgg.store.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;


@RestController
@RequestMapping("/carts")
public class CartController extends BaseController{
    @Autowired
    private CartService cartService;

    @RequestMapping("/add_to_cart")
    public JsonResult<Void> addToCart(Integer pid, Integer amount, HttpSession session){
        cartService.addToCart(getuidFromSession(session),pid,amount,getUsernameFromSession(session));
        return new JsonResult<>(OK);
    }
    @RequestMapping({"","/"})
    public JsonResult<List<CartVo>> getVOByUid(HttpSession session){
        List<CartVo> data = cartService.getVOByUid(getuidFromSession(session));
        return new JsonResult<>(OK,data);
    }

    @RequestMapping("/{cid}/num/add")
    public JsonResult<Integer> addNum(HttpSession session,@PathVariable("cid") Integer cid){
        Integer data = cartService.addNum(cid, getuidFromSession(session), getUsernameFromSession(session));
        return new JsonResult<>(OK,data);
    }
    @RequestMapping("/list")
    //默认是数组形式传递，如果需要传递list，则需要使用@RequestParam注解
    public JsonResult<List<CartVo>> getVOByCid(@RequestParam List<Integer> cids, HttpSession session){
        List<CartVo> data = cartService.getVOByCid(cids,getuidFromSession(session));
        return new JsonResult<>(OK,data);
    }
    @RequestMapping("/{cid}/num/reduce")
    public JsonResult<Integer> reduceNum(HttpSession session,@PathVariable("cid") Integer cid){
        Integer data = cartService.reduceNum(cid, getuidFromSession(session), getUsernameFromSession(session));
        return new JsonResult<>(OK,data);
    }

    @RequestMapping("/{cid}/delete")
    public JsonResult<Void> deleteByCid(@PathVariable("cid") Integer cid,HttpSession session){
        cartService.deleteByCid(cid,getuidFromSession(session));
        return new JsonResult<>(OK);
    }
}
