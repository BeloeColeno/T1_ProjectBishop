package com.java.bishopproject.bishopprototype.controller;

import com.java.bishopproject.bishopprototype.service.BishopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final BishopService bishopService;

    @GetMapping("/queue-size")
    public ResponseEntity<Integer> getQueueSize() {
        return ResponseEntity.ok(bishopService.getCurrentQueueSize());
    }

    @GetMapping("/tasks-completed")
    public ResponseEntity<Map<String, Integer>> getTasksCompleted() {
        return ResponseEntity.ok(bishopService.getTasksCompletedByAuthor());
    }
}
