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

    @PostMapping
    @WeylandWatchingYou(mode = WeylandWatchingYou.AuditMode.KAFKA)
    public ResponseEntity<String> submitCommand(@Valid @RequestBody CommandModel command) {
        bishopService.processCommand(command);
        return ResponseEntity.ok("Command submitted successfully");
    }

    @GetMapping("/queue-size")
    public ResponseEntity<Integer> getQueueSize() {
        return ResponseEntity.ok(bishopService.getCurrentQueueSize());
    }
}
