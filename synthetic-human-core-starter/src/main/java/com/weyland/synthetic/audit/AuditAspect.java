package com.weyland.synthetic.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {
    private final KafkaAuditSender kafkaSender;

    @Around("@annotation(weylandWatchingYou)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, WeylandWatchingYou weylandWatchingYou) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        Object[] args = joinPoint.getArgs();

        String startMessage = "Method " + methodName + " started with args: " + Arrays.toString(args);
        logAudit(startMessage, weylandWatchingYou.mode());

        try {
            Object result = joinPoint.proceed();

            String successMessage = "Method " + methodName + " completed successfully with result: " + (result != null ? result.toString() : "null");

            logAudit(successMessage, weylandWatchingYou.mode());

            return result;
        } catch (Throwable t) {
            String errorMessage = "Method " + methodName + " failed with error: " + t.getMessage();
            logAudit(errorMessage, weylandWatchingYou.mode());
            throw t;
        }
    }

    private void logAudit(String message, WeylandWatchingYou.AuditMode mode) {
        if (mode == WeylandWatchingYou.AuditMode.CONSOLE) {
            log.info("[AUDIT] " + message);
        } else if (mode == WeylandWatchingYou.AuditMode.KAFKA) {
            kafkaSender.sendAuditMessage(message);
        }
    }
}
