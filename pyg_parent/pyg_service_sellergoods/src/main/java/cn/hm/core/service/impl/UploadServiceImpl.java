package cn.hm.core.service.impl;

import cn.hm.core.service.UploadService;
import cn.hm.core.utils.FastDFSClient;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UploadServiceImpl implements UploadService {

    @Override
    public String uploadFile(byte[] bytes, String originalFilename, long size) {
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            return fastDFSClient.uploadFile(bytes, originalFilename, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
