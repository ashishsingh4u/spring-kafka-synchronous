package com.techienotes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {

    @Value("${kafka.group.id}")
    private String groupId;

    @Value("${kafka.reply.topic}")
    private String replyTopic;

    @Bean
    public ReplyingKafkaTemplate<String, OrderInfo, BillingInfo> replyingKafkaTemplate(ProducerFactory<String, OrderInfo> pf,
                                                                                ConcurrentKafkaListenerContainerFactory<String, BillingInfo> factory) {
        System.out.println("Creating ReplyingKafkaTemplate for request/response");
        ConcurrentMessageListenerContainer<String, BillingInfo> replyContainer = factory.createContainer(replyTopic);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(groupId);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public KafkaTemplate<String, BillingInfo> replyTemplate(ProducerFactory<String, BillingInfo> pf,
                                                       ConcurrentKafkaListenerContainerFactory<String, BillingInfo> factory) {
        System.out.println("Creating KafkaTemplate for reply");
        KafkaTemplate<String, BillingInfo> kafkaTemplate = new KafkaTemplate<>(pf);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;
    }
}