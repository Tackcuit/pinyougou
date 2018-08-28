package cn.hm.core.controller;

import cn.hm.core.pojo.address.Address;
import cn.hm.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    @RequestMapping("/findListByLoginUser")
    public List<Address> findListByLoginUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findListByUserId(userId);
    }

}
