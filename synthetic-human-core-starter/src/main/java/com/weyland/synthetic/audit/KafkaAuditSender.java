package com.weyland.synthetic.audit;

import org.springframework.kafka.core.KafkaTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

public class KafkaAuditSender {
    private static final Logger logger = Logger.getLogger(KafkaAuditSender.class.getName());
    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaAuditSender(String topic, KafkaTemplate<String, String> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuditMessage(String message) {
        if (kafkaTemplate != null) {
            logger.log(Level.INFO, "Sending audit to Kafka topic [" + topic + "]: " + message);
            kafkaTemplate.send(topic, message);
        } else {
            logger.info("[Kafka Unreachable, Audit:] " + message);
        }
    }
}
