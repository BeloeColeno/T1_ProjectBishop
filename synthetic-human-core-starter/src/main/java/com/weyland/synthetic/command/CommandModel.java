package com.weyland.synthetic.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommandModel {
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1000, message = "Описание не может превышать 1000 символов")
    private String description;

    private CommandPriority priority;

    @NotBlank(message = "Имя автора не может быть пустым")
    @Size(max = 100, message = "Имя автора не может превышать 100 символов")
    private String author;

    @NotBlank(message = "Время не может быть пустым")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", shape = JsonFormat.Shape.STRING)
    private String time;

    public enum CommandPriority {
        COMMON, CRITICAL
    }
}
