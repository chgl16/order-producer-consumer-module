# order-producer-consumer-module
    基于RabbitMQ消息中间件的订单投递消费模块，订单生产者和消费者，AMQP架构核心原理解析，消息可靠性方案
## 1. 环境
![Spring Boot](https://img.shields.io/badge/SpringBoot-2.1.5-green.svg)&nbsp;&nbsp;
![amqp](https://img.shields.io/badge/AMQP-starter-blue.svg)&nbsp;&nbsp;
![web](https://img.shields.io/badge/Web-starter-red.svg)&nbsp;&nbsp;
![test](https://img.shields.io/badge/Test-starter-yellow.svg)&nbsp;&nbsp;
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.6.10-orange.svg)

## 2. 运行
* 先启动RabbitMQ服务端
```bash
sudo rabbitmq-server start &
```
* 以web形式启动消费者模块，自动注册对应交换机，队列
```bash
mvn spring-boot:run
```
* 以test形式使用生产者模块发送消息
```bash
mvn -Dtest=ProducerApplicationTests#contextLoads test 
```

## 3. 实现
1. application.yml配置
```yml
# RabbitMQ配置
spring:
  rabbitmq:
    addresses: 127.0.0.1
    port: 15672
    username: root
    password: mima
    connection-timeout: 15000
    listener:   # 消费者配置
      simple:
        concurrency: 5
        max-concurrency: 10
        acknowledge-mode: manual  # 手动签收消息

# 服务路径和端口配置
server:
  servlet:
    context-path: /
  port: 8002
```
> 以上是消费者的配置，除了RabbitMQ的服务器信息外主要就是线程问题和签收类型。
  
>生产者模块配置简单，因为只是使用单元测试发布订单消息，只需要配置服务器信息即可

2. 订单Order类
```java
/**
 * 订单，作为发送的消息
 * @author chgl16
 * @date 2019-05-16 21:21
 * @version 1.0
 */
@Component("order")
public class Order implements Serializable {
    private static final long serialVersionUID = -2926828973935247000L;

    private String id;

    private String name;

    /**
     * 存储消息发送的唯一标识
     */
    private String messageId;

    /** setter and getter **/
}
```
>生产者和消费者都的对象，需要保证一致以序列化和反序列化
3. 消费者消费消息核心代码
```java
/**
 * 接收消费订单消息
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
    @RabbitHandler  // 标识为消息消费者
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
```
> *@RabbitHandler*仅仅表明其是一个消息消费者，*@RabbitListener*注解可以绑定消费者到某特定交换机队列（不存在就会创建）  

> *@Payload Order order*这里Spring内部对从服务器队列获取到的Queue做反序列化，额外定义的Channel用于手工确认ACK反馈。

4. 生产者生成订单发送到队列
```java
/**
 * 生成消息，发送
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
```
> 直接使用Spring提供操作的*RabbitTemplate*模板，*convertAndSend*有多个构造方法，注意选择使用。

5. 因为消费者提前打开，所有一旦消费者发送订单消息，立即被消费，服务端*localhost:15672*显示的order-queue队列消息数是都为0的。当然如果关闭消费者，前提服务器端有消费者发送的指定交换机和相应绑定的队列，这时可见队列存在未消费的消息。

## 4. 注意
* 在消费者反序列化Order对象时除了需要保证两边的Order类一样，*SerialVersionUID*一致外，还需要包名一致，不然一直报错*ClassNotFound*。
* Durable的交换机和队列重启也会保存注册。
* *xyz.cglzwz.xxApplication*大包下的测试类不能注入*xyz.cglzwz.common*大包下的bean。
* 一般选用手动ACK确认，因此消费者也需要使用Channel，即*channel.basicAck(deliveryTag, false);*


## 5. 参考
* [RabbitMQ官网文档](https://www.rabbitmq.com/documentation.html)
* [慕课网-RabbitMQ入门与实战](https://www.imooc.com/learn/1042)
* 咕泡学院RabbitMQ中间件
