package com.weyland.synthetic.worker;

import com.weyland.synthetic.command.CommandExecutor;
import com.weyland.synthetic.command.CommandModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Component
@Slf4j
public class SyntheticWorkersPool {

    @Value("${synthetic.weyland.workers.count:3}")
    private int workerCount;

    @Value("${synthetic.weyland.workers.task-execution-time-seconds:5}")
    private int taskExecutionTimeSeconds;

    @Getter
    private final List<SyntheticWorker> workers = new ArrayList<>();
    private final AtomicInteger currentTasksCount = new AtomicInteger(0);
    private final AtomicInteger completedTasksCount = new AtomicInteger(0);
    private final ScheduledExecutorService statusReporter = Executors.newScheduledThreadPool(1);

    private final ConcurrentHashMap<String, AtomicInteger> tasksByAuthor = new ConcurrentHashMap<>();

    @Autowired
    @Lazy
    private CommandExecutor commandExecutor;

    @PostConstruct
    public void init() {
        log.info("Инициализация пула синтетиков с {} рабочими", workerCount);

        Consumer<CommandModel> taskHandler = command -> {
            if (command != null && command.getAuthor() != null) {
                tasksByAuthor.computeIfAbsent(command.getAuthor(),
                        k -> new AtomicInteger(0)).incrementAndGet();
                log.debug("Задача от автора {} зарегистрирована", command.getAuthor());
            }
        };

        for (int i = 0; i < workerCount; i++) {
            SyntheticWorker worker = new SyntheticWorker("Worker-" + (i + 1));
            worker.setTaskCompletionHandler(taskHandler);
            workers.add(worker);
            log.debug("Создан синтетик {}", worker);
        }

        if (commandExecutor != null) {
            commandExecutor.setImmediateExecutor(this::executeImmediately);
            commandExecutor.setQueuedExecutor(this::executeQueued);
            log.info("Интеграция с CommandExecutor настроена успешно");
        } else {
            log.warn("CommandExecutor недоступен, выполнение команд будет ограничено");
        }

        statusReporter.scheduleAtFixedRate(this::reportStatus, 10, 30, TimeUnit.SECONDS);
        log.info("Инициализация SyntheticWorkersPool завершена успешно");
    }

    private void executeImmediately(CommandModel command) {
        log.info("Processing critical command from {}: {}", command.getAuthor(), command.getDescription());

        boolean executed = executeTask(command);
        if (!executed) {
            log.warn("No available synthetic workers for critical command. Adding to queue.");
            executeQueued(command);
        }
    }

    private void executeQueued(CommandModel command) {
        log.info("Processing common command from {}: {}", command.getAuthor(), command.getDescription());

        boolean executed = false;
        int retries = 0;
        final int MAX_RETRIES = 3;

        while (!executed && retries < MAX_RETRIES) {
            executed = executeTask(command);

            if (!executed) {
                log.warn("No available synthetic workers. Waiting before retry {} of {}",
                        retries + 1, MAX_RETRIES);
                try {
                    Thread.sleep(5000); // 5 секунд перед повторной попыткой
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                retries++;
            }
        }

        if (!executed) {
            log.error("Failed to execute command after {} retries: {}",
                    MAX_RETRIES, command.getDescription());
        }
    }

    public boolean executeTask(CommandModel command) {
        SyntheticWorker availableWorker = findAvailableWorker();

        if (availableWorker != null) {
            currentTasksCount.incrementAndGet();

            new Thread(() -> {
                try {
                    long executionTimeMillis = taskExecutionTimeSeconds * 1000L;
                    availableWorker.executeTask(command);

                    log.info("Worker {} is executing task: {}, duration: {} seconds",
                            availableWorker, command.getDescription(), taskExecutionTimeSeconds);

                    Thread.sleep(executionTimeMillis);

                    availableWorker.completeTask();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Task execution interrupted", e);
                } finally {
                    currentTasksCount.decrementAndGet();
                    completedTasksCount.incrementAndGet();
                }
            }).start();

            return true;
        }

        return false;
    }

    private SyntheticWorker findAvailableWorker() {
        return workers.stream()
                .filter(worker -> !worker.isBusy())
                .findFirst()
                .orElse(null);
    }

    private void reportStatus() {
        int busyWorkers = getBusyWorkersCount();
        log.info("Workers status: {}/{} busy, completed tasks: {}",
                busyWorkers, workers.size(), completedTasksCount.get());
    }

    public int getAvailableWorkersCount() {
        return (int) workers.stream()
                .filter(worker -> !worker.isBusy())
                .count();
    }

    public int getBusyWorkersCount() {
        return (int) workers.stream()
                .filter(SyntheticWorker::isBusy)
                .count();
    }

    public int getCompletedTasksCount() {
        return completedTasksCount.get();
    }

    public int getCurrentTasksCount() {
        return currentTasksCount.get();
    }

    public ConcurrentHashMap<String, Integer> getTasksByAuthor() {
        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        tasksByAuthor.forEach((author, counter) -> result.put(author, counter.get()));
        return result;
    }
}
