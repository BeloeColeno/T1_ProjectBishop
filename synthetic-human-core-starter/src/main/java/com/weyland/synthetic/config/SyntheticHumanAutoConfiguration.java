package com.weyland.synthetic.config;

import com.weyland.synthetic.audit.AuditAspect;
import com.weyland.synthetic.audit.KafkaAuditSender;
import com.weyland.synthetic.command.CommandExecutor;
import com.weyland.synthetic.monitoring.SyntheticMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableConfigurationProperties(SyntheticHumanProperties.class)
@EnableAspectJAutoProxy
public class SyntheticHumanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CommandExecutor commandExecutor(SyntheticHumanProperties properties) {
        return new CommandExecutor(
                properties.getCommand().getCorePoolSize(),
                properties.getCommand().getMaxPoolSize(),
                properties.getCommand().getQueueCapacity()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaAuditSender kafkaAuditSender(SyntheticHumanProperties properties, KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaAuditSender(properties.getAudit().getKafkaTopic(), kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditAspect auditAspect(KafkaAuditSender kafkaSender) {
        return new AuditAspect(kafkaSender);
    }

    @Bean
    @ConditionalOnMissingBean
    public SyntheticMetrics syntheticMetrics(CommandExecutor commandExecutor, MeterRegistry meterRegistry) {
        SyntheticMetrics metrics = new SyntheticMetrics(meterRegistry, commandExecutor);
        metrics.initMetrics();
        return metrics;
    }
}
