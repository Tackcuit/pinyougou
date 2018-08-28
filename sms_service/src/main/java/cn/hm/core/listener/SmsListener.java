package cn.hm.core.listener;

import cn.hm.core.utils.SmsUtil;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String, String> map) {
        try {
            //调用根据类, 接收短信内容, 发送出去
            SendSmsResponse response = smsUtil.sendSms(
                    map.get("mobile"),
                    map.get("template_code"),
                    map.get("sign_name"),
                    map.get("param"));
            System.err.println("-----------------------------------------------------");
            System.err.println(map.get("mobile"));
            System.err.println("Code=" + response.getCode());
            System.err.println("Message=" + response.getMessage());
            System.err.println("RequestId=" + response.getRequestId());
            System.err.println("BizId=" + response.getBizId());
            System.err.println("-----------------------------------------------------");
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }

}
