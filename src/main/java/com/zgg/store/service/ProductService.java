package com.zgg.store.service;

import com.zgg.store.entity.Product;

import java.util.List;


public interface ProductService {
    List<Product> findHotList();
    Product findById(Integer id);
}
