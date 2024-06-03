package com.zgg.store.service;

import com.zgg.store.vo.CartVo;

import java.util.List;


public interface CartService {
    /**
     * 将商品添加到购物车中
     * @param uid 用户id
     * @param pid   商品id
     * @param amount    新增数量
     * @param username  用户名
     */
    void addToCart(Integer uid,Integer pid,Integer amount,String username);

    List<CartVo> getVOByUid(Integer uid);

    /**
     *更新用户的购物车数据的数量
     * @param cid
     * @param uid
     * @param username
     * @return 增加后的数量
     */
    Integer addNum(Integer cid,Integer uid,String username);

    List<CartVo> getVOByCid(List<Integer> cids,Integer uid);

    Integer reduceNum(Integer cid,Integer uid,String username);

    /**
     * 删除购物车中的商品
     * @param cid   购物车id
     * @param uid   用户id
     */
    void deleteByCid(Integer cid,Integer uid);
}
