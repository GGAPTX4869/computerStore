package com.zgg.store.service.impl;

import com.zgg.store.entity.District;
import com.zgg.store.mapper.DistrictMapper;
import com.zgg.store.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DistrictServiceImpl implements DistrictService {
    @Autowired
    private DistrictMapper districtMapper;
    @Override
    public List<District> getByParent(String parent) {
        List<District> list = districtMapper.findByParent(parent);
        /**
         * 在进行网络数据传输时，为了尽量避免无效数据的传递，可以将无效数据设置为null
         *可以节省流量，另一方面提升了效率
         */
        for (District district : list) {
            district.setId(null);
            district.setParent(null);
        }
        return list;
    }

    @Override
    public String getNameByCode(String code) {
        String nameByCode = districtMapper.findNameByCode(code);
        return nameByCode;
    }
}
