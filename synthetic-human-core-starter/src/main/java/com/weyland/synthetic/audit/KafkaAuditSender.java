package com.weyland.synthetic.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaAuditSender {
    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendAuditMessage(String message) {
        if (kafkaTemplate != null) {
            log.info("Sending audit to Kafka topic [" + topic + "]: " + message);
            kafkaTemplate.send(topic, message);
        } else {
            log.info("[Kafka Unreachable, Audit:] " + message);
        }
    }
}
