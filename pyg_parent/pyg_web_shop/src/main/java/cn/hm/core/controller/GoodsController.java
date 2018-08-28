package cn.hm.core.controller;

import cn.hm.core.pojo.entity.GoodsEntity;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.pojo.good.Goods;
import cn.hm.core.service.GoodsService;
import cn.hm.core.utils.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods, Integer page, Integer rows) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(name);
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

    @RequestMapping("/save")
    public Result save(@RequestBody GoodsEntity goodsEntity) {
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsEntity.getGoods().setSellerId(userName);
            int save = goodsService.save(goodsEntity);
            if (save > 0) {
                return new Result(true, "保存成功");
            } else {
                return new Result(false, "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }
    }

    @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity) {
        try {
            int update = goodsService.update(goodsEntity);
            if (update > 0) {
                return new Result(true, "更新成功");
            } else {
                return new Result(false, "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }

    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id) {
        return goodsService.findOne(id);
    }


}
