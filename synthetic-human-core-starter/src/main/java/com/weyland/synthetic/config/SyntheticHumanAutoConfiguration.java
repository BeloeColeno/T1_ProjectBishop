package com.weyland.synthetic.config;

import com.weyland.synthetic.audit.AuditAspect;
import com.weyland.synthetic.audit.KafkaAuditSender;
import com.weyland.synthetic.command.CommandExecutor;
import com.weyland.synthetic.monitoring.SyntheticMetrics;
import com.weyland.synthetic.worker.SyntheticWorkersPool;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.context.annotation.Primary;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableConfigurationProperties(SyntheticHumanProperties.class)
@EnableAspectJAutoProxy
@Slf4j
public class SyntheticHumanAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Primary
    public CommandExecutor commandExecutor(SyntheticHumanProperties properties) {
        log.info("Создание CommandExecutor");
        return new CommandExecutor(
                properties.getCommand().getCorePoolSize(),
                properties.getCommand().getMaxPoolSize(),
                properties.getCommand().getQueueCapacity()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    @Primary
    public SyntheticWorkersPool syntheticWorkersPool() {
        log.info("Создание пула синтетических работников");
        return new SyntheticWorkersPool();
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaAuditSender kafkaAuditSender(SyntheticHumanProperties properties, KafkaTemplate<String, String> kafkaTemplate) {
        log.info("Создание отправителя аудита в Kafka");
        return new KafkaAuditSender(properties.getAudit().getKafkaTopic(), kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditAspect auditAspect(KafkaAuditSender kafkaSender) {
        log.info("Создание аспекта аудита");
        return new AuditAspect(kafkaSender);
    }

    @Bean
    @ConditionalOnMissingBean
    @Primary
    public SyntheticMetrics syntheticMetrics(CommandExecutor commandExecutor, MeterRegistry meterRegistry) {
        log.info("Создание метрик синтетиков");
        SyntheticMetrics metrics = new SyntheticMetrics(meterRegistry, commandExecutor);

        metrics.initMetrics();

        log.info("Метрики синтетиков успешно созданы");
        return metrics;
    }
}
