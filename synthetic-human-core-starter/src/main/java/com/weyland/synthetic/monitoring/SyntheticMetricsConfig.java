package com.weyland.synthetic.monitoring;

import com.weyland.synthetic.command.CommandExecutor;
import com.weyland.synthetic.worker.SyntheticWorkersPool;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
