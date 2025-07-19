package com.java.bishopproject.bishopprototype.controller;

import com.java.bishopproject.bishopprototype.service.BishopService;
import com.weyland.synthetic.audit.WeylandWatchingYou;
import com.weyland.synthetic.command.CommandModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commands")
@RequiredArgsConstructor
public class CommandController {

    private final BishopService bishopService;

    @PostMapping("/console-audit")
    public ResponseEntity<String> processCommandConsole(@Valid @RequestBody CommandModel command) {
        bishopService.processCommandConsole(command);
        return ResponseEntity.ok("Команда обработана с аудитом в консоль");
    }

    @PostMapping("/kafka-audit")
    public ResponseEntity<String> processCommandKafka(@Valid @RequestBody CommandModel command) {
        bishopService.processCommandKafka(command);
        return ResponseEntity.ok("Команда обработана с аудитом в Kafka");
    }

    @GetMapping("/queue-size")
    public ResponseEntity<Integer> getQueueSize() {
        return ResponseEntity.ok(bishopService.getCurrentQueueSize());
    }
}
