package com.telran.notificationservice.blocking;
import jakarta.persistence.OptimisticLockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LockingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LockingAspect.class);

    @Around("@annotation(lockable)")
    public Object retry(ProceedingJoinPoint joinPoint, Lockable lockable) throws Throwable {
        int maxRetries = lockable.maxRetries();
        long delay = lockable.delay();

        int attempt = 0;
        while (true) {
            try {
                attempt++;
                return joinPoint.proceed();
            } catch (OptimisticLockException e) {
                if (attempt > maxRetries) {
                    logger.error("Exceeded max retries for method: {} after {} attempts", joinPoint.getSignature(), attempt);
                    throw e;
                }
                logger.warn("OptimisticLockException on method: {}. Retrying {}/{}", joinPoint.getSignature(), attempt, maxRetries);
                Thread.sleep(delay);
            }
        }
    }
}