package cn.hm.core.controller;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.pojo.seller.Seller;
import cn.hm.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.zookeeper.data.Id;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @RequestMapping("/save")
    public Result save(@RequestBody Seller seller) {
        try {
            int i = sellerService.save(seller);
            if (i > 0) {
                return new Result(true, "保存成功");
            } else {
                return new Result(false, "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }
    }

}
