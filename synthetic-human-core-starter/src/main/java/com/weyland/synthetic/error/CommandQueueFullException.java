package com.weyland.synthetic.error;

public class CommandQueueFullException extends RuntimeException {
    public CommandQueueFullException(String message) {
        super(message);
    }
}
