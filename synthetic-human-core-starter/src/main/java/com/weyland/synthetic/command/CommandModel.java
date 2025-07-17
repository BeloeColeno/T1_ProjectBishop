package com.weyland.synthetic.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommandModel {
    @NotBlank
    @Size(max = 1000)
    private String description;

    private CommandPriority priority;

    @NotBlank
    @Size(max = 100)
    private String author;

    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String time;

    public enum CommandPriority {
        COMMON, CRITICAL
    }
}
