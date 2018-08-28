package cn.hm.core.service;


import cn.hm.core.pojo.user.User;

public interface UserService {


    void getSmsCode(String phone) throws Exception;

    boolean checkCode(String phone,String smsCode);

    int save(User user);
}
