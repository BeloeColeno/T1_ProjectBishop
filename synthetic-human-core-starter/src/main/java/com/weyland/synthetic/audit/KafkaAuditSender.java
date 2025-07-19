package com.weyland.synthetic.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class KafkaAuditSender {
    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendAuditMessage(String message) {
        if (kafkaTemplate != null) {
            try {
                log.info("Sending audit to Kafka topic [" + topic + "]: " + message);

                CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

                future.whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Sent message='{}' to topic='{}'", message, topic);
                    } else {
                        log.error("Unable to send message='{}' to topic='{}'", message, topic, ex);
                    }
                });
            } catch (Exception e) {
                log.error("Error while sending audit message to Kafka", e);
                log.info("[Kafka Error, Fallback Audit:] " + message);
            }
        } else {
            log.info("[Kafka Unreachable, Audit:] " + message);
        }
    }
}
