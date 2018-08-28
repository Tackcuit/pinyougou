package cn.hm.core.controller;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.pojo.template.TypeTemplate;
import cn.hm.core.service.TemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TemplateService templateService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody TypeTemplate typeTemplate, Integer page, Integer rows) {
        return templateService.findPage(typeTemplate, page, rows);
    }

    @RequestMapping("/findAll")
    public PageResult findAll() {
        return templateService.findAll();
    }

    @RequestMapping("/save")
    public Result save(@RequestBody TypeTemplate typeTemplate) {
        try {
            int save = templateService.save(typeTemplate);
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
    public Result update(@RequestBody TypeTemplate typeTemplate) {
        try {
            int update = templateService.update(typeTemplate);
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
            int delete = templateService.delete(ids);
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
    public TypeTemplate findOne(Long id) {
        return templateService.findOne(id);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return templateService.selectOptionList();
    }

}
