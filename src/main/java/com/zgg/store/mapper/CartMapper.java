package com.zgg.store.mapper;

import com.zgg.store.entity.Cart;
import com.zgg.store.vo.CartVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface CartMapper {
    /**
     * 插入购物车数据
     * @param cart  购物车数据
     * @return  受影响的行数
     */
    Integer insert(Cart cart);

    /**
     * 更新购物车某件商品的数量
     * @param cid   购物车数据id
     * @param num   更新的数量
     * @param modifiedUser  修改者
     * @param modifiedTime  修改时间
     * @return  受影响的行数
     */
    Integer updateNumByCid(Integer cid, Integer num, String modifiedUser, Date modifiedTime);

    /**
     * 根据用户的id和商品的id来查询购物车中的数据
     * @param uid   用户id
     * @param pid   商品id
     * @return  购物车中的数据
     */
    Cart findByUidAndPid(Integer uid,Integer pid);

    /**
     * 查询用户购物车中的数据
     * @param uid   用户id
     * @return  用户的购物车数据
     */
    List<CartVo> findVOByUid(Integer uid);

    /**
     * 根据购物车cid的值来查询数据是否存在
     * @param cid   购物车商品的id
     * @return  查询到的购物车内的数据
     */
    Cart findByCid(Integer cid);

    /**
     * 根据购物车中勾选的商品的cid来查询
     * @param cids 商品的cid集合
     * @return  查询到的勾选的数据
     */
    List<CartVo> findVOByCid(@Param("cids") List<Integer> cids);

    /**
     * 删除购物车中的商品
     * @param cid 购物车的id
     * @return  受影响的行数
     */
    Integer deleteByCid(Integer cid);
}
