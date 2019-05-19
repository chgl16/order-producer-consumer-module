package xyz.cglzwz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.cglzwz.common.entity.Order;
import xyz.cglzwz.sender.OrderSender;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerApplicationTests {
    @Autowired
    private Order order;

    @Autowired
    private OrderSender orderSender;

    /**
     * 测试发送
     */
    @Test
    public void contextLoads() throws Exception {
        order.setId("002");
        order.setName("测试订单");
        order.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        orderSender.send(order);
        System.out.println("消息发送成功....");
    }


}
