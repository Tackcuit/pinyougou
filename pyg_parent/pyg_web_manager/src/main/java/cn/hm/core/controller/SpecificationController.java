package cn.hm.core.controller;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.pojo.entity.SpecEntity;
import cn.hm.core.pojo.specification.Specification;
import cn.hm.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    @RequestMapping("/findAll")
    public List<Specification> findAll() {
        return specificationService.findAll();
    }

    @RequestMapping("/search")
    public PageResult Search(@RequestBody Specification specification, Integer page, Integer rows) {
        return specificationService.Search(specification, page, rows);
    }

    @RequestMapping("/save")
    public Result save(@RequestBody SpecEntity specEntity) {
        try {
            int save = specificationService.save(specEntity);
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
    public Result update(@RequestBody SpecEntity specEntity) {
        try {
            int update = specificationService.update(specEntity);
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

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            int delete = specificationService.delete(ids);
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
    public SpecEntity findOne(Long id) {
        return specificationService.findOne(id);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return specificationService.selectOptionList();
    }

//    @RequestMapping("/selectOptionList")
//    public List<SpecificationOption> selectOptionList(){
//        return specificationService.selectOptionList();
//    }

}
