package com.java.bishopproject.bishopprototype.service;

import com.weyland.synthetic.audit.WeylandWatchingYou;
import com.weyland.synthetic.command.CommandExecutor;
import com.weyland.synthetic.command.CommandModel;
import com.weyland.synthetic.monitoring.SyntheticMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BishopService {

    private final CommandExecutor commandExecutor;
    private final SyntheticMetrics syntheticMetrics;

    @WeylandWatchingYou(mode = WeylandWatchingYou.AuditMode.CONSOLE)
    public void processCommandConsole(CommandModel command) {
        commandExecutor.executeCommand(command);
        syntheticMetrics.incrementTasksCompletedForAuthor(command.getAuthor());
    }

    @WeylandWatchingYou(mode = WeylandWatchingYou.AuditMode.KAFKA)
    public void processCommandKafka(CommandModel command) {
        commandExecutor.executeCommand(command);
        syntheticMetrics.incrementTasksCompletedForAuthor(command.getAuthor());
    }

    public Map<String, Integer> getTasksCompletedByAuthor() {
        return syntheticMetrics.getTasksCompletedByAuthor();
    }

    public int getCurrentQueueSize() {
        return syntheticMetrics.getCurrentQueueSize();
    }
}
