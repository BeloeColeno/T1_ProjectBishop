package com.weyland.synthetic.command;

import com.weyland.synthetic.error.CommandQueueFullException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Component
@Slf4j
public class CommandExecutor {

    @Getter
    private final ThreadPoolExecutor executor;
    private final int queueCapacity;

    private final AtomicInteger totalTasksExecuted = new AtomicInteger(0);
    private final AtomicInteger activeTasksCount = new AtomicInteger(0);

    private Consumer<CommandModel> immediateExecutor;
    private Consumer<CommandModel> queuedExecutor;

    public CommandExecutor(
            @Value("${synthetic.command.executor.core-pool-size:2}") int corePoolSize,
            @Value("${synthetic.command.executor.max-pool-size:5}") int maxPoolSize,
            @Value("${synthetic.command.executor.queue-capacity:100}") int queueCapacity) {

        this.queueCapacity = queueCapacity;
        this.executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity)
        );

        this.immediateExecutor = command ->
                log.info("Executing critical command from {}: {}", command.getAuthor(), command.getDescription());

        this.queuedExecutor = command ->
                log.info("Executing common command from {}: {}", command.getAuthor(), command.getDescription());

        log.info("CommandExecutor initialized with core pool size: {}, max pool size: {}, queue capacity: {}",
                corePoolSize, maxPoolSize, queueCapacity);
    }

    public void setImmediateExecutor(Consumer<CommandModel> executor) {
        if (executor != null) {
            this.immediateExecutor = executor;
        }
    }

    public void setQueuedExecutor(Consumer<CommandModel> executor) {
        if (executor != null) {
            this.queuedExecutor = executor;
        }
    }

    public void executeCommand(CommandModel command) {
        activeTasksCount.incrementAndGet();

        if (command.getPriority() == CommandModel.CommandPriority.CRITICAL) {
            executeImmediately(command);
        } else {
            enqueueCommand(command);
        }
    }

    private void executeImmediately(CommandModel command) {
        try {
            log.debug("Executing critical command immediately");
            immediateExecutor.accept(command);
            totalTasksExecuted.incrementAndGet();
        } finally {
            activeTasksCount.decrementAndGet();
        }
    }

    private void enqueueCommand(CommandModel command) {
        try {
            executor.execute(() -> {
                try {
                    log.debug("Executing queued command");
                    queuedExecutor.accept(command);
                } finally {
                    totalTasksExecuted.incrementAndGet();
                    activeTasksCount.decrementAndGet();
                }
            });
        } catch (RejectedExecutionException e) {
            activeTasksCount.decrementAndGet();
            throw new CommandQueueFullException("Command queue is full. Maximum capacity: " + queueCapacity);
        }
    }

    public int getCurrentQueueSize() {
        return activeTasksCount.get();
    }

    public int getTotalTasksExecuted() {
        return totalTasksExecuted.get();
    }
}
