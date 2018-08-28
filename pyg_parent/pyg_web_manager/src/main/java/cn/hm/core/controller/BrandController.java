package cn.hm.core.controller;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.pojo.good.Brand;
import cn.hm.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<Brand> getAll() {
        return brandService.getAll();
    }

    @RequestMapping("/findPage")
    public PageResult getPage(Integer page, Integer rows) {
        return brandService.getPage(page, rows);
    }

    @RequestMapping("/save")
    public Result save(@RequestParam Brand brand) {
        try {
            int i = brandService.save(brand);
            if (i > 0) {
                return new Result(true, "保存成功");
            } else {
                return new Result(false, "保存失败,可能存在参数错误.请检查.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败,存在未知错误,请联系技术解决.");
        }
    }

    @RequestMapping("/update")
    public Result update(@RequestParam Brand brand) {
        try {
            int i = brandService.update(brand);
            if (i > 0) {
                return new Result(true, "更新成功");
            } else {
                return new Result(false, "更新失败,可能存在参数错误.请检查.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败,存在未知错误,请联系技术解决.");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            int i = brandService.delete(ids);
            if (i > 0) {
                return new Result(true, "删除成功");
            } else {
                return new Result(false, "删除失败,可能存在参数错误.请检查.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败,存在未知错误,请联系技术解决.");
        }
    }

    @RequestMapping("/findOne")
    public Brand getOne(Long id) {
        return brandService.getOne(id);
    }

    @RequestMapping("/search")
    public PageResult searchPage(@RequestBody Brand brand, Integer page, Integer rows) {
        return brandService.searchPage(brand, page, rows);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }

}
