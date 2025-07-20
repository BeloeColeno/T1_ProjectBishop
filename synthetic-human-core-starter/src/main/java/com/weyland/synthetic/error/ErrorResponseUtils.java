package com.weyland.synthetic.error;

import java.util.Map;

/**
 * Для того, что-бы поддерживать минимальную зависимость от Spring
 * я постарался сделать класс, что-бы обрабатывать ошибки самостоятельно.
 * Это конечно оверкод, но я пытался не пихать спринг веб библиотеки в свой стартер.
 * Просто что-бы он меньше был связан с другими библиотеками.
 */
public class ErrorResponseUtils {

    public static ErrorResponse createValidationErrorResponse(String message, Map<String, String> validationErrors) {
        return ErrorResponse.of(message, "VALIDATION_ERROR", validationErrors);
    }

    public static ErrorResponse createQueueFullErrorResponse(String message, Map<String, String> validationErrors) {
        return ErrorResponse.of(message, "QUEUE_FULL_ERROR", validationErrors);
    }

    public static ErrorResponse createInvalidArgumentErrorResponse(String message, Map<String, String> validationErrors) {
        return ErrorResponse.of(message, "INVALID_ARGUMENT", validationErrors);
    }

    public static ErrorResponse createGeneralErrorResponse(String message, Map<String, String> validationErrors) {
        return ErrorResponse.of(message, "GENERAL_ERROR", validationErrors);
    }
}
