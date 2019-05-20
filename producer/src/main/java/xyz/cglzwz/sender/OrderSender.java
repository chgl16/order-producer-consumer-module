package xyz.cglzwz.sender;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.cglzwz.common.entity.Order;

/**
 * 生产者生成消息，发送
 * @author chgl16
 * @date 2019-05-16 21:27
 * @version 1.0
 */
@Component
public class OrderSender {
    /**
     * 使用提供的集成模板操作
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Order order) throws Exception {
        // 设置相关消息唯一标识
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(order.getMessageId());
        rabbitTemplate.convertAndSend(
                "order-exchange", // exchange
                "order.chgl16", // routing key
                order, // message
                correlationData  // 唯一标识
        );
    }
}
