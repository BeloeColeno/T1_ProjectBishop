package com.weyland.synthetic.error;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    String message;
    String code;
    LocalDateTime timestamp;

    public static ErrorResponse of(String message, String code) {
        return new ErrorResponse(message, code, LocalDateTime.now());
    }
}

