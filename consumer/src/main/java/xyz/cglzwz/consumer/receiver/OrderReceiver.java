package xyz.cglzwz.consumer.receiver;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import xyz.cglzwz.common.entity.Order;

import java.util.Map;

/**
 * 消费者接收消息消费
 * @author chgl16
 * @date 2019-05-17 10:04
 * @version 1.0
 */
@Component
public class OrderReceiver {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order-queue", durable = "true"),
            exchange = @Exchange(value = "order-exchange", type = "topic"),
            key = "order.*"
        )
    )
    @RabbitHandler  // 标识为消息接受者
    public void receive(@Payload Order order,
                        Channel channel,  // 手动确认需要使用channel
                        @Headers Map<String, Object> headers
                        ) throws Exception {
        System.err.println("-------接收消息，开始消费-------");
        System.err.println("订单ID: " + order.getId());
        // 从Header获取确认标识
        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
        // 手动确认ACK
        channel.basicAck(deliveryTag, false);
    }
}
