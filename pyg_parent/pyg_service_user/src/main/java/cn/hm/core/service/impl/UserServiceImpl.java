package cn.hm.core.service.impl;

import cn.hm.core.dao.user.UserDao;
import cn.hm.core.pojo.user.User;
import cn.hm.core.service.UserService;
import cn.hm.core.utils.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.MapMessage;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQQueue smsDestination;

    @Value("${template_code}")
    private String template_code;

    @Value("${sign_name}")
    private String sign_name;

    @Override
    public void getSmsCode(String phone) {

        long smscode = (long) (Math.random() * 10000);

        redisTemplate.boundHashOps(Constants.SMS_REDIS).put(phone, smscode);

        jmsTemplate.send(smsDestination, (session) -> {
            MapMessage message = session.createMapMessage();
            message.setString("mobile", phone);//手机号
            message.setString("template_code", template_code);//模板编码
            message.setString("sign_name", sign_name);//签名
            Map map = new HashMap();
            map.put("code", smscode);    //验证码
            message.setString("param", JSON.toJSONString(map));
            return message;
        });

    }

    @Override
    public boolean checkCode(String phone, String smsCode) {
        String redisCode = String.valueOf(redisTemplate.boundHashOps(Constants.SMS_REDIS).get(phone));
        if (redisCode.equals(smsCode.trim())) {
            return true;
        }
        return false;
    }

    @Override
    public int save(User user) {
        int i = userDao.insertSelective(user);
        return i;
    }

}
