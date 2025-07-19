package com.weyland.synthetic.audit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableAspectJAutoProxy
public class AuditConfig {
    @Value("${synthetic.weyland.audit.kafka.topic:weyland-audit-topic}")
    private String auditTopic;

    @Bean
    @ConditionalOnProperty(name = "synthetic.weyland.audit.kafka.enabled", havingValue = "true")
    public KafkaAuditSender kafkaAuditSender(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaAuditSender(auditTopic, kafkaTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "synthetic.weyland.audit.kafka.enabled", havingValue = "false", matchIfMissing = true)
    public KafkaAuditSender consoleAuditSender() {
        return new KafkaAuditSender("console", null);
    }

    @Bean
    public AuditAspect auditAspect(KafkaAuditSender kafkaAuditSender) {
        return new AuditAspect(kafkaAuditSender);
    }
}
