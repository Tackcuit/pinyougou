package cn.hm.core.service;

public interface UploadService {
    String uploadFile(byte[] bytes, String originalFilename, long size);
}
