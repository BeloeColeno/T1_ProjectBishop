package com.weyland.synthetic.error;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    String message;
    String code;
    LocalDateTime timestamp;
}
