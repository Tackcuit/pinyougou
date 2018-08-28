package cn.hm.core.controller;

import cn.hm.core.pojo.entity.GoodsEntity;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.pojo.good.Goods;
import cn.hm.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;


    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods, Integer page, Integer rows) {
        return goodsService.search(goods, page, rows);
    }

    @RequestMapping("/findAll")
    public List<Goods> findAll() {
        return goodsService.findAll();
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            int delete = goodsService.delete(ids);
//            int deleteSolr = itemManagerService.deleteItemFromSolr(ids);
            if (delete > 0) {
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
    public GoodsEntity findOne(Long id) {
        return goodsService.findOne(id);
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            int updateStatus = goodsService.updateStatus(ids, status);
//            int itemToSolr = itemManagerService.itemToSolr(ids);
//            int page = cmsService.createPage(ids);
            if (updateStatus > 0) {
                return new Result(true, "审核成功");
            } else {
                return new Result(false, "审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败");
        }
    }


}
