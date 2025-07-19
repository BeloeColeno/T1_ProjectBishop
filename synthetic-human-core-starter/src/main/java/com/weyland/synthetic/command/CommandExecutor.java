package com.weyland.synthetic.command;

import com.weyland.synthetic.error.CommandQueueFullException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CommandExecutor {

    @Getter
    private final ThreadPoolExecutor executor;
    private final int queueCapacity;

    private final AtomicInteger totalTasksExecuted = new AtomicInteger(0);

    private final AtomicInteger activeTasksCount = new AtomicInteger(0);

    public CommandExecutor(int corePoolSize, int maxPoolSize, int queueCapacity) {
        this.queueCapacity = queueCapacity;
        this.executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity)
        );
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
        log.info("Executing critical command from " + command.getAuthor() + ": " + command.getDescription());
        // Тут как бы что-то делается
        totalTasksExecuted.incrementAndGet();
        activeTasksCount.decrementAndGet();
    }

    private void enqueueCommand(CommandModel command) {
        try {
            executor.execute(() -> {
                try {
                    log.info("Executing common command from " + command.getAuthor() + ": " + command.getDescription());
                    // Тут тоже что-то делается
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
