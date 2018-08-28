package cn.hm.core.controller;

import cn.hm.core.pojo.ad.Content;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/findAll")
    public List<Content> findAll() {
        return contentService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows) {
        return contentService.findPage(null, page, rows);
    }

    @RequestMapping("/save")
    public Result save(@RequestBody Content content) {
        try {
            int save = contentService.save(content);
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

    @RequestMapping("/findOne")
    public Content findOne(Long id) {
        return contentService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody Content content) {
        try {
            int update = contentService.update(content);
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
            int delete = contentService.delete(ids);
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

    @RequestMapping("/search")
    public PageResult search(@RequestBody Content content, Integer page, Integer rows) {
        return contentService.findPage(content, page, rows);
    }

}