package cn.hm.core.controller;

import cn.hm.core.pojo.entity.Result;
import cn.hm.core.service.UploadService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class UploadControcller {

    @Reference
    private UploadService uploadService;

    @Value("${FILE_SERVER_URL}")
    private String ip;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) {
        try {
            String url = uploadService.uploadFile(file.getBytes(), file.getOriginalFilename(), file.getSize());
            return new Result(true, ip + url);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }

}
