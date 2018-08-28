package cn.hm.core.controller;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.pojo.seller.Seller;
import cn.hm.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller, Integer page, Integer rows) {
        return sellerService.search(seller, page, rows);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody Seller seller) {
        try {
            int i = sellerService.update(seller);
            if (i > 0) {
                return new Result(true, "更新成功");
            } else {
                return new Result(false, "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(String[] ids) {
        try {
            int i = sellerService.delete(ids);
            if (i > 0) {
                return new Result(true, "删除成功");
            } else {
                return new Result(false, "删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    @RequestMapping("/findOne")
    public Seller findOne(String id) {
        return sellerService.findOne(id);
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId, String status) {
        try {
            int i = sellerService.updateStatus(sellerId, status);
            if (i > 0) {
                return new Result(true, "更改成功");
            } else {
                return new Result(false, "更改失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更改失败");
        }
    }


}
