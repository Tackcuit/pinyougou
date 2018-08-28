package cn.hm.core.listener;

import cn.hm.core.service.ItemManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;

public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemManagerService itemManagerService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
        try {
            //获取数据
            String idsText = msg.getText();
            //拆分为数组
            String[] idTexts = idsText.substring(1, idsText.length() - 1).split(",");
            //转换为Long[]
            Long[] ids = new Long[idsText.length()];
            for (int i = 0; i < idTexts.length; i++) {
                ids[i] = Long.parseLong(idTexts[i]);
            }
            int itemToSolr = itemManagerService.deleteItemFromSolr(ids);
            if (itemToSolr > 0) {
                System.err.println("---------------------------------------------------------------------");
                System.err.println(idsText);
                System.err.println("删除solr索引成功");
                System.err.println("---------------------------------------------------------------------");
            } else {
                System.err.println("err------------------------------------------------------------------");
                System.err.println(idsText);
                System.err.println("删除solr索引失败");
                System.err.println("err------------------------------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
