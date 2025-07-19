package com.weyland.synthetic.monitoring;

import com.weyland.synthetic.command.CommandExecutor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class SyntheticMetrics {
    private final MeterRegistry registry;
    private final CommandExecutor commandExecutor;
    private final ConcurrentHashMap<String, Counter> tasksByAuthor = new ConcurrentHashMap<>();
    private Counter totalTasksCounter;

    public void initMetrics() {
        Gauge.builder("synthetic.tasks.queue.size", commandExecutor::getCurrentQueueSize)
                .description("Current number of tasks in queue")
                .register(registry);

        totalTasksCounter = Counter.builder("synthetic.tasks.completed")
                .description("Total number of tasks completed")
                .register(registry);
    }

    public void incrementTasksCompletedForAuthor(String author) {
        tasksByAuthor
                .computeIfAbsent(author, a -> Counter.builder("synthetic.tasks.completed.author")
                        .tag("author", author)
                        .description("Number of tasks completed by author")
                        .register(registry))
                .increment();

        totalTasksCounter.increment();
    }

    public int getCurrentQueueSize() {
        return commandExecutor.getCurrentQueueSize();
    }

    public Map<String, Integer> getTasksCompletedByAuthor() {
        Map<String, Integer> result = new HashMap<>();
        tasksByAuthor.forEach((author, counter) -> result.put(author, (int) counter.count()));
        return result;
    }
}
