package com.zgg.store.controller;

import com.zgg.store.entity.District;
import com.zgg.store.service.DistrictService;
import com.zgg.store.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/districts")
public class DistrictController extends BaseController {
    @Autowired
    private DistrictService districtService;

    //districts开头的请求都被拦截到getByParent()方法
    @RequestMapping({"/",""})
    public JsonResult<List<District>> getByParent(String parent){
        List<District> data = districtService.getByParent(parent);
        return new JsonResult<>(OK,data);
    }
}
