package com.weyland.synthetic.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
public class AuditAspect {
    private static final Logger logger = Logger.getLogger(AuditAspect.class.getName());
    private final KafkaAuditSender kafkaSender;

    public AuditAspect(KafkaAuditSender kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

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
            logger.info("[AUDIT] " + message);
        } else if (mode == WeylandWatchingYou.AuditMode.KAFKA) {
            kafkaSender.sendAuditMessage(message);
        }
    }
}
