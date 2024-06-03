package com.zgg.store.service.impl;

import com.zgg.store.entity.Product;
import com.zgg.store.mapper.ProductMapper;
import com.zgg.store.service.ProductService;
import com.zgg.store.service.ex.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Override
    public List<Product> findHotList() {
        List<Product> hotList = productMapper.findHotList();
        for (Product product : hotList) {
            product.setPriority(null);
            product.setCreatedUser(null);
            product.setCreatedTime(null);
            product.setModifiedUser(null);
            product.setModifiedTime(null);
        }
        return hotList;
    }

    @Override
    public Product findById(Integer id) {
        Product product = productMapper.findById(id);
        if (product==null){
            throw new ProductNotFoundException("尝试访问的商品数据不存在");
        }
        product.setPriority(null);
        product.setCreatedUser(null);
        product.setCreatedTime(null);
        product.setModifiedUser(null);
        product.setModifiedTime(null);
        return product;
    }

}
