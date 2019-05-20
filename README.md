# order-producer-consumer-module
    基于RabbitMQ消息中间件的订单投递消费模块，订单生产者和消费者，AMQP架构核心原理解析，消息可靠性方案
## 1. 环境
![Spring Boot](https://img.shields.io/badge/SpringBoot-2.1.5-green.svg)
![amqp](https://img.shields.io/badge/AMQP-starter-blue.svg)
![web](https://img.shields.io/badge/Web-starter-red.svg)
![test](https://img.shields.io/badge/Test-starter-yellow.svg)
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

## 3. 流程


## 4. 核心


## 5. 注意