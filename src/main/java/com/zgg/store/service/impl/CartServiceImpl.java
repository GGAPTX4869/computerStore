package com.zgg.store.service.impl;

import com.zgg.store.entity.Cart;
import com.zgg.store.mapper.CartMapper;
import com.zgg.store.mapper.ProductMapper;
import com.zgg.store.service.CartService;
import com.zgg.store.service.ex.*;
import com.zgg.store.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Override
    public void addToCart(Integer uid, Integer pid, Integer amount, String username) {
        //查询当前要添加的这个商品是否已存在于购物车中
        Cart result = cartMapper.findByUidAndPid(uid, pid);
        Date date = new Date();//保证时间一致
        //如果存在，则更新购物车中的num
        if(result!=null){
            Integer rows = cartMapper.updateNumByCid(result.getCid(), result.getNum()+amount, username, date);
            if(rows!=1){
                throw new UpdateException("更新数据时出现未知的异常");
            }
        }else {//如果不存在，则插入商品
            Cart cart = new Cart();
            cart.setPid(pid);
            cart.setNum(amount);
            cart.setUid(uid);
            cart.setPrice(productMapper.findById(pid).getPrice());//价格直接从商品表中获取
            cart.setCreatedTime(date);
            cart.setCreatedUser(username);
            cart.setModifiedTime(date);
            cart.setModifiedUser(username);
            Integer insert = cartMapper.insert(cart);
            if(insert !=1){
                throw new InsertException("插入数据时产生未知的异常");
            }
        }
    }

    @Override
    public List<CartVo> getVOByUid(Integer uid) {
        return cartMapper.findVOByUid(uid);
    }

    @Override
    public Integer addNum(Integer cid, Integer uid, String username) {
        Cart result = cartMapper.findByCid(cid);
        if(result == null){
            throw new CartNotFoundException("数据不存在");
        }
        if(!result.getUid().equals(uid)){
            throw new AccessDeniedException("数据非法访问");
        }
        Integer integer = cartMapper.updateNumByCid(cid, result.getNum() + 1, username, new Date());
        if (integer!=1){
            throw new UpdateException("更新数据失败");
        }
        return result.getNum() + 1;
    }

    @Override
    public List<CartVo> getVOByCid(List<Integer> cids, Integer uid) {
        List<CartVo> list = cartMapper.findVOByCid(cids);
        Iterator<CartVo> it = list.iterator();
        while (it.hasNext()){
            CartVo cartVo = it.next();
            if(!cartVo.getUid().equals(uid)){
                //从集合中移除这个元素
                //必须使用迭代器的remove
                it.remove();
            }
        }
        return list;
    }

    @Override
    public Integer reduceNum(Integer cid, Integer uid, String username) {
        Cart result = cartMapper.findByCid(cid);
        if(result == null){
            throw new CartNotFoundException("数据不存在");
        }
        if(!result.getUid().equals(uid)){
            throw new AccessDeniedException("数据非法访问");
        }
        Integer integer = cartMapper.updateNumByCid(cid, result.getNum() - 1, username, new Date());
        if (integer!=1){
            throw new UpdateException("更新数据失败");
        }
        return result.getNum() - 1;
    }

    @Override
    public void deleteByCid(Integer cid, Integer uid) {
        Cart result = cartMapper.findByCid(cid);
        if (result==null){
            throw new CartNotFoundException("数据不存在");
        }
        if (!result.getUid().equals(uid)){
            throw new AccessDeniedException("数据非法访问");
        }
        Integer rows = cartMapper.deleteByCid(cid);
        if (rows!= 1){
            throw new DeleteException("删除数据失败");
        }
    }
}
