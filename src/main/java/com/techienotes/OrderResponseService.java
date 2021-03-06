package com.techienotes;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class OrderResponseService {

    @KafkaListener(topics = "${kafka.request.topic}", groupId = "${kafka.group.id}")
    @SendTo
    public BillingInfo handle(OrderInfo orderInfo) {
        System.out.println("Calculating Result at MicroService#2...");
        double total = ThreadLocalRandom.current().nextDouble(2.5, 9.9);
        BillingInfo billingInfo = new BillingInfo();
        billingInfo.setId(orderInfo.getId());
        billingInfo.setAmount(total);
        billingInfo.setDescription("Bill generated by Microservice#2");
        return billingInfo;
    }
}