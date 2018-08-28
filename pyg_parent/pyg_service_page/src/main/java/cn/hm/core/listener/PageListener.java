package cn.hm.core.listener;

import cn.hm.core.service.CmsService;
import cn.hm.core.service.ItemManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Arrays;

public class PageListener implements MessageListener {

    @Autowired
    private CmsService cmsService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
        try {
            //获取数据
            String idsText = msg.getText();
            //拆分为数组
            String[] idTexts = idsText.substring(1, idsText.length() - 1).split(",");
            //转换为Long[]
            Long[] ids = Arrays.stream(idTexts).map(Long::valueOf).toArray(Long[]::new);
            int page = cmsService.createPage(ids);
            if (page > 0) {
                System.err.println("---------------------------------------------------------------------");
                System.err.println(idsText);
                System.err.println("创建静态页面成功");
                System.err.println("---------------------------------------------------------------------");
            } else {
                System.err.println("err------------------------------------------------------------------");
                System.err.println(idsText);
                System.err.println("创建静态页面失败");
                System.err.println("err------------------------------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
