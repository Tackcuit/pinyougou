package cn.hm.core.controller;

import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.pojo.specification.SpecificationOption;
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

    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id) {
        return templateService.findOne(id);
    }

    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id) {
        return templateService.findBySpecList(id);
    }

}
