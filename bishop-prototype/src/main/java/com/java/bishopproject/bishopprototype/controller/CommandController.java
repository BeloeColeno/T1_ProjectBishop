package com.java.bishopproject.bishopprototype.controller;

import com.java.bishopproject.bishopprototype.service.BishopService;
import com.weyland.synthetic.command.CommandModel;
import com.weyland.synthetic.error.ErrorResponse;
import com.weyland.synthetic.error.ErrorResponseUtils;
import com.weyland.synthetic.error.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/commands")
@RequiredArgsConstructor
public class CommandController {

    private final BishopService bishopService;

    @PostMapping("/console-audit")
    public ResponseEntity<?> processCommandConsole(@RequestBody CommandModel command) {
        // Это кастомная валидация из стартера, можно было бы сделать на @Valid, но я запарился
        Map<String, String> errors = ValidationUtils.validateCommand(command);
        if (!errors.isEmpty()) {
            ErrorResponse errorResponse = ErrorResponseUtils.createValidationErrorResponse(
                    "Ошибка валидации входных данных", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        bishopService.processCommandConsole(command);
        return ResponseEntity.ok("Команда обработана с аудитом в консоль");
    }

    @PostMapping("/kafka-audit")
    public ResponseEntity<?> processCommandKafka(@RequestBody CommandModel command) {
        // Это кастомная валидация из стартера, можно было бы сделать на @Valid, но я запарился
        Map<String, String> errors = ValidationUtils.validateCommand(command);
        if (!errors.isEmpty()) {
            ErrorResponse errorResponse = ErrorResponseUtils.createValidationErrorResponse(
                    "Ошибка валидации входных данных", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        bishopService.processCommandKafka(command);
        return ResponseEntity.ok("Команда обработана с аудитом в Kafka");
    }

    @GetMapping("/queue-size")
    public ResponseEntity<Integer> getQueueSize() {
        return ResponseEntity.ok(bishopService.getCurrentQueueSize());
    }
}
