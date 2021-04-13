package com.techienotes;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "v1")
public class KafkaController {

    @Value("${kafka.request.topic}")
    private String requestTopic;

    @Autowired
    private ReplyingKafkaTemplate<String, OrderInfo, BillingInfo> replyingKafkaTemplate;

    @PostMapping("/get-billing")
    public ResponseEntity<OrderInfo> getObject(@RequestBody OrderRequest orderRequest)
            throws InterruptedException, ExecutionException {
        System.out.println("Request received at MicroService1");
        OrderInfo orderInfo = new OrderInfo(orderRequest.id, "Object created at Microservice#1", null);
        ProducerRecord<String, OrderInfo> record = new ProducerRecord<>(requestTopic, null, "STD001", orderInfo);
        RequestReplyFuture<String, OrderInfo, BillingInfo> future = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, BillingInfo> response = future.get();
        orderInfo.setBillingInfo(response.value());
        return new ResponseEntity<>(orderInfo, HttpStatus.OK);
    }
}