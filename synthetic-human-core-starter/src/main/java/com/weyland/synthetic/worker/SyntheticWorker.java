package com.weyland.synthetic.worker;

import com.weyland.synthetic.command.CommandModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class SyntheticWorker {

    private final String id;
    @Getter
    private boolean busy = false;
    @Getter
    private String currentTask = null;

    @Setter
    private Consumer<CommandModel> taskCompletionHandler;

    public synchronized boolean executeTask(CommandModel command) {
        if (busy) {
            return false;
        }

        busy = true;
        currentTask = command.getDescription();
        log.info("[{}] Starting task: {} from author: {}", id, command.getDescription(), command.getAuthor());

        if (taskCompletionHandler != null) {
            try {
                taskCompletionHandler.accept(command);
            } catch (Exception e) {
                log.warn("[{}] Error in task completion handler: {}", id, e.getMessage());
            }
        }

        return true;
    }

    public synchronized void completeTask() {
        if (busy) {
            log.info("[{}] Task completed: {}", id, currentTask);
            busy = false;
            currentTask = null;
        }
    }

    @Override
    public String toString() {
        return id + (busy ? " (занят)" : " (свободен)");
    }
}
