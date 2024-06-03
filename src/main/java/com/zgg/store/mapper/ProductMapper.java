package com.zgg.store.mapper;

import com.zgg.store.entity.Product;

import java.util.List;


public interface ProductMapper {
    List<Product>findHotList();

    Product findById(Integer id);
}
