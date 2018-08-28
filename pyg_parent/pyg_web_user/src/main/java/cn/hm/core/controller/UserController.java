package cn.hm.core.controller;

import cn.hm.core.pojo.entity.Result;
import cn.hm.core.pojo.user.User;
import cn.hm.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/sendCode")
    public Result getSmsCode(String phone) {
        try {
            userService.getSmsCode(phone);
            return new Result(true, "发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "发送失败");
        }

    }


    @RequestMapping("/save")
    public Result save(@RequestParam("smscode") String smsCode, @RequestBody User user) {
        try {
            if (userService.checkCode(user.getPhone(), smsCode)) {
                user.setCreated(new Date());
                user.setUpdated(new Date());
                user.setStatus("Y");
                user.setSourceType("1");
                int save = userService.save(user);
                if (save > 0) {
                    return new Result(true, "注册成功");
                }
            }
            return new Result(false, "验证码错误");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败");
        }
    }

}
