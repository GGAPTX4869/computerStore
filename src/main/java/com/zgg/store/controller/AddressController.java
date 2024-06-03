package com.zgg.store.controller;

import com.zgg.store.entity.Address;
import com.zgg.store.service.AddressService;
import com.zgg.store.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;


@RestController
@RequestMapping("/addresses")
public class AddressController extends BaseController {
    @Autowired
    private AddressService addressService;

    @RequestMapping("/add_new_address")
    public JsonResult<Void> addNewAddress(Address address, HttpSession session){
        addressService.addNewAddress(getuidFromSession(session),getUsernameFromSession(session),address);
        return new JsonResult<>(OK);
    }

    @RequestMapping({"/",""})
    public JsonResult<List<Address>> getByUid(HttpSession session){
        List<Address> data = addressService.getByUid(getuidFromSession(session));
        return new JsonResult<>(OK,data);
    }

    @RequestMapping("/{aid}/set_default")
    public JsonResult<Void> setFault(@PathVariable("aid") Integer aid, HttpSession session){
        addressService.setDefault(aid,getuidFromSession(session),getUsernameFromSession(session));
        return new JsonResult<>(OK);
    }

    @RequestMapping("/{aid}/delete")
    public JsonResult<Void> delet(@PathVariable("aid") Integer aid,HttpSession session){
        addressService.delete(aid,getuidFromSession(session),getUsernameFromSession(session));
        return new JsonResult<>(OK);
    }
}
