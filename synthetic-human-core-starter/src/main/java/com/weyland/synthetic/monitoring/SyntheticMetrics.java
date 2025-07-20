package com.weyland.synthetic.monitoring;

import com.weyland.synthetic.command.CommandExecutor;
import com.weyland.synthetic.worker.SyntheticWorkersPool;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SyntheticMetrics {
    private final MeterRegistry registry;
    private final CommandExecutor commandExecutor;
    private final ConcurrentHashMap<String, Counter> tasksByAuthor = new ConcurrentHashMap<>();
    private Counter totalTasksCounter;

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    public SyntheticMetrics(MeterRegistry registry, CommandExecutor commandExecutor) {
        this.registry = registry;
        this.commandExecutor = commandExecutor;
        log.info("SyntheticMetrics создан с регистратором {} и исполнителем команд {}",
                registry.getClass().getSimpleName(), commandExecutor.getClass().getSimpleName());
    }

    @PostConstruct
    public void setupPoolMetrics() {
        if (applicationContext != null) {
            try {
                SyntheticWorkersPool pool = applicationContext.getBean(SyntheticWorkersPool.class);
                if (pool != null) {
                    log.info("Регистрация метрик для пула синтетиков с {} рабочими", pool.getWorkers().size());
                    registerPoolMetrics(pool);
                }
            } catch (Exception e) {
                log.warn("Не удалось получить пул синтетиков для метрик: {}", e.getMessage());
            }
        }
    }

    private void registerPoolMetrics(SyntheticWorkersPool pool) {
        try {
            Gauge.builder("synthetic.workers.available", pool::getAvailableWorkersCount)
                    .description("Количество доступных андроидов")
                    .register(registry);
            log.info("Метрика synthetic.workers.available зарегистрирована");

            Gauge.builder("synthetic.workers.busy", pool::getBusyWorkersCount)
                    .description("Количество занятых андроидов")
                    .register(registry);
            log.info("Метрика synthetic.workers.busy зарегистрирована");

            Gauge.builder("synthetic.workers.total", () -> pool.getWorkers().size())
                    .description("Общее количество андроидов")
                    .register(registry);
            log.info("Метрика synthetic.workers.total зарегистрирована");
        } catch (Exception e) {
            log.error("Ошибка при регистрации метрик андроидов: {}", e.getMessage());
        }
    }

    public void initMetrics() {
        log.info("Инициализация основных метрик");

        try {
            Gauge.builder("synthetic.tasks.queue.size", commandExecutor::getCurrentQueueSize)
                    .description("Текущее количество задач в очереди")
                    .register(registry);
            log.info("Метрика synthetic.tasks.queue.size зарегистрирована");

            totalTasksCounter = Counter.builder("synthetic.tasks.completed")
                    .description("Общее количество выполненных задач")
                    .register(registry);
            log.info("Метрика synthetic.tasks.completed зарегистрирована");
        } catch (Exception e) {
            log.error("Ошибка при регистрации основных метрик: {}", e.getMessage());
        }

        log.info("Инициализация основных метрик завершена");
    }

    public void incrementTasksCompletedForAuthor(String author) {
        if (author == null || author.isBlank()) {
            log.warn("Попытка увеличить счетчик задач для пустого автора");
            return;
        }

        log.debug("Увеличение счетчика задач для автора: {}", author);

        try {
            tasksByAuthor
                    .computeIfAbsent(author, a -> Counter.builder("synthetic.tasks.completed.author")
                            .tag("author", author)
                            .description("Количество выполненных задач по автору")
                            .register(registry))
                    .increment();

            if (totalTasksCounter != null) {
                totalTasksCounter.increment();
            }

            log.debug("Счетчик задач успешно увели��ен для автора: {}", author);
        } catch (Exception e) {
            log.error("Ошибка при увеличении счетчика задач для автора {}: {}", author, e.getMessage());
        }
    }

    public int getCurrentQueueSize() {
        return commandExecutor.getCurrentQueueSize();
    }

    public Map<String, Integer> getTasksCompletedByAuthor() {
        Map<String, Integer> result = new HashMap<>();

        tasksByAuthor.forEach((author, counter) -> {
            int count = (int) counter.count();
            result.put(author, count);
            log.debug("Автор {} выполнил {} задач", author, count);
        });

        if (applicationContext != null) {
            try {
                SyntheticWorkersPool pool = applicationContext.getBean(SyntheticWorkersPool.class);
                if (pool != null) {
                    pool.getTasksByAuthor().forEach((author, count) ->
                        result.merge(author, count, Integer::sum));
                }
            } catch (Exception e) {
                log.debug("Не удалось дополнить статистику данными из пула: {}", e.getMessage());
            }
        }

        log.debug("Получены данные о выполненных задачах по {} авторам", result.size());
        return result;
    }
}
