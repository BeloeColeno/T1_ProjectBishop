package com.java.bishopproject.bishopprototype.service;

import com.weyland.synthetic.audit.WeylandWatchingYou;
import com.weyland.synthetic.command.CommandExecutor;
import com.weyland.synthetic.command.CommandModel;
import com.weyland.synthetic.monitoring.SyntheticMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public int getCurrentQueueSize() {
        return commandExecutor.getCurrentQueueSize();
    }
}
