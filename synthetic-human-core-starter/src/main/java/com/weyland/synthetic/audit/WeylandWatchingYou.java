package com.weyland.synthetic.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WeylandWatchingYou {
    AuditMode mode() default AuditMode.CONSOLE;

    enum AuditMode {
        CONSOLE, KAFKA
    }
}
