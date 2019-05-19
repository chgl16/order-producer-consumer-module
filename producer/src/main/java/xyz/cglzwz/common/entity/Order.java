package xyz.cglzwz.common.entity;

import org.springframework.stereotype.Component;

import java.io.Serializable;

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

    public Order() {}

    public Order(String id, String name, String messageId) {
        this.id = id;
        this.name = name;
        this.messageId = messageId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
