package com.weyland.synthetic.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SyntheticMetricsConfig {

    @Bean
    @ConditionalOnMissingBean
    public MeterRegistry meterRegistry() {
        log.info("Создан стандартный SimpleMeterRegistry для метрик синтетиков");
        return new SimpleMeterRegistry();
    }
}
