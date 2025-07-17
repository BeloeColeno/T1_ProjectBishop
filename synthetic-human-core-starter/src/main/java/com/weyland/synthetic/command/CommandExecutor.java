package com.weyland.synthetic.command;

import com.weyland.synthetic.error.CommandQueueFullException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CommandExecutor {
    private static final Logger logger = Logger.getLogger(CommandExecutor.class.getName());

    private final ThreadPoolExecutor executor;
    private final int queueCapacity;

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
        if (command.getPriority() == CommandModel.CommandPriority.CRITICAL) {
            executeImmediately(command);
        } else {
            enqueueCommand(command);
        }
    }

    private void executeImmediately(CommandModel command) {
        logger.info("Executing critical command from " + command.getAuthor() + ": " + command.getDescription());
        // Тут как бы что-то делается
    }

    private void enqueueCommand(CommandModel command) {
        try {
            executor.execute(() -> {
                logger.info("Executing common command from " + command.getAuthor() + ": " + command.getDescription());
                // Тут тоже что-то делается
            });
        } catch (RejectedExecutionException e) {
            throw new CommandQueueFullException("Command queue is full. Maximum capacity: " + queueCapacity);
        }
    }

    public int getCurrentQueueSize() {
        return executor.getQueue().size();
    }

}
