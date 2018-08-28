package cn.hm.core.controller;

import cn.hm.core.pojo.ad.ContentCategory;
import cn.hm.core.pojo.entity.PageResult;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.service.ContentCateoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCateoryController {

    @Reference
    private ContentCateoryService contentCateoryService;

    @RequestMapping("/save")
    public Result save(@RequestBody ContentCategory contentCategory) {
        try {
            int save = contentCateoryService.save(contentCategory);
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

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            int delete = contentCateoryService.delete(ids);
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

    @RequestMapping("/update")
    public Result update(@RequestBody ContentCategory contentCategory) {
        try {
            int update = contentCateoryService.update(contentCategory);
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

    @RequestMapping("/search")
    public PageResult search(@RequestBody ContentCategory contentCategory, Integer page, Integer rows) {
        return contentCateoryService.search(contentCategory, page, rows);
    }

    @RequestMapping("/findAll")
    public List<ContentCategory> findAll() {
        return contentCateoryService.findAll();
    }

    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id) {
        return contentCateoryService.findOne(id);
    }

}
