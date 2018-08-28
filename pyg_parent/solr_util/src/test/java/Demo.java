import cn.hm.core.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext*.xml"})
public class Demo {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void test() {

        redisUtil.ItemCatToRedis();
//        redisUtil.TemplateToRedis();
    }


}
