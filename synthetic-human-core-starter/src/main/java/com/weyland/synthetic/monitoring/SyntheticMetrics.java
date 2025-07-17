package com.weyland.synthetic.monitoring;

import com.weyland.synthetic.command.CommandExecutor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class SyntheticMetrics {
    private final MeterRegistry registry;
    private final CommandExecutor commandExecutor;
    private final ConcurrentHashMap<String, Counter> tasksByAuthor = new ConcurrentHashMap<>();

    public void initMetrics() {
        Gauge.builder("synthetic.tasks.queue.size", commandExecutor::getCurrentQueueSize)
                .description("Current number of tasks in queue")
                .register(registry);
    }

    public void incrementTasksCompletedForAuthor(String author) {
        tasksByAuthor
                .computeIfAbsent(author, a -> Counter.builder("synthetic.tasks.completed")
                        .tag("author", author)
                        .description("Number of tasks completed by author")
                        .register(registry))
                .increment();
    }
}
