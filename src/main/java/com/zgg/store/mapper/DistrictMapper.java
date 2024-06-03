package com.zgg.store.mapper;

import com.zgg.store.entity.District;

import java.util.List;


public interface DistrictMapper {
    /**
     * 根据父代号查询区域信息
     * @param parent    父代号
     * @return  某个父区域下的所有区域列表
     */
    List<District> findByParent(String parent);

    /**
     * 根据当前code来获取省市区的名称
     * @param code  当前code
     * @return  省市区的名称
     */
    String findNameByCode(String code);
}
