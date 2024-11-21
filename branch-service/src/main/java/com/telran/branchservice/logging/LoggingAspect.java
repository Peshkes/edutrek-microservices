package com.telran.branchservice.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            logger.info("Method {} in {} executed in {} ms",
                    joinPoint.getSignature().getName(),
                    joinPoint.getTarget().getClass().getSimpleName(),
                    executionTime);

            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("Method {} in {} threw exception after {} ms: {}",
                    joinPoint.getSignature().getName(),
                    joinPoint.getTarget().getClass().getSimpleName(),
                    executionTime,
                    throwable.getMessage());
            throw throwable;
        }
    }

    @Before("@annotation(Loggable)")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Method {} in {} started with arguments: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getSimpleName(),
                Arrays.toString(joinPoint.getArgs()));
    }

//    @After("@annotation(Loggable)")
//    public void logAfter(JoinPoint joinPoint) {
//        logger.info("Method {} in {} finished",
//                joinPoint.getSignature().getName(),
//                joinPoint.getTarget().getClass().getSimpleName());
//    }

//    @AfterThrowing(pointcut = "@annotation(Loggable)", throwing = "exception")
//    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
//        logger.error("Exception in method {} in {}: {}",
//                joinPoint.getSignature().getName(),
//                joinPoint.getTarget().getClass().getSimpleName(),
//                exception.getMessage());
//    }
}