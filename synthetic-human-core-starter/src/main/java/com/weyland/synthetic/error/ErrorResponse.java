package com.weyland.synthetic.error;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private String message;
    private String code;
    private LocalDateTime timestamp;
    private Map<String, String> details;

    public static ErrorResponse of(String message, String code, Map<String, String> details) {
        return ErrorResponse.builder()
                .message(message)
                .code(code)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
    }
}
