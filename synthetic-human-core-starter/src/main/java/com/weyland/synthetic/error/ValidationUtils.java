package com.weyland.synthetic.error;

import com.weyland.synthetic.command.CommandModel;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final int MAX_DESCRIPTION_LENGTH = 1000;
    private static final int MAX_AUTHOR_LENGTH = 100;
    private static final Pattern ISO_DATE_PATTERN =
        Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$");

    public static Map<String, String> validateCommand(CommandModel command) {
        Map<String, String> errors = new HashMap<>();

        // Validate description
        if (command.getDescription() == null || command.getDescription().isBlank()) {
            errors.put("description", "Описание не может быть пустым");
        } else if (command.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            errors.put("description", "Описание не может превышать " + MAX_DESCRIPTION_LENGTH + " символов");
        }

        // Validate priority
        if (command.getPriority() == null) {
            errors.put("priority", "Приоритет не может быть пустым");
        }

        // Validate author
        if (command.getAuthor() == null || command.getAuthor().isBlank()) {
            errors.put("author", "Имя автора не может быть пустым");
        } else if (command.getAuthor().length() > MAX_AUTHOR_LENGTH) {
            errors.put("author", "Имя автора не может превышать " + MAX_AUTHOR_LENGTH + " символов");
        }

        // Validate time format
        if (command.getTime() == null || command.getTime().isBlank()) {
            errors.put("time", "Время не может быть пустым");
        } else if (!ISO_DATE_PATTERN.matcher(command.getTime()).matches()) {
            errors.put("time", "Время должно соответствовать формату ISO-8601 (yyyy-MM-dd'T'HH:mm:ss.SSS'Z')");
        }

        return errors;
    }
}
