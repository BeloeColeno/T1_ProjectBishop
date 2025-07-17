package com.weyland.synthetic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "synthetic")
public class SyntheticHumanProperties {

    private CommandProperties command = new CommandProperties();
    private AuditProperties audit = new AuditProperties();

    @Data
    public static class CommandProperties {
        private int corePoolSize = 2;
        private int maxPoolSize = 5;
        private int queueCapacity = 100;
    }

    @Data
    public static class AuditProperties {
        private String kafkaTopic = "weyland-audit-topic";
    }
}
