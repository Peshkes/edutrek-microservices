package com.telran.groupservice.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String requestMethod = request.getMethod();
                String requestURI = request.getRequestURI();
                String requestId = request.getHeader("X-Request-Id");

                logger.info("RequestId: {} - Method {} in {} started with arguments: {}. Request: {} {}",
                        requestId,
                        joinPoint.getSignature().getName(),
                        joinPoint.getTarget().getClass().getSimpleName(),
                        Arrays.toString(joinPoint.getArgs()),
                        requestMethod, requestURI);

                Object result = joinPoint.proceed();
                long executionTime = System.currentTimeMillis() - start;

                logger.info("RequestId: {} - Method {} in {} executed in {} ms. Request: {} {}",
                        requestId,
                        joinPoint.getSignature().getName(),
                        joinPoint.getTarget().getClass().getSimpleName(),
                        executionTime,
                        requestMethod, requestURI);

                return result;
            }
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - start;
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String requestMethod = request.getMethod();
                String requestURI = request.getRequestURI();
                String requestId = request.getHeader("X-Request-Id");

                logger.error("RequestId: {} - Method {} in {} threw exception after {} ms. Request: {} {}. Exception: {}",
                        requestId,
                        joinPoint.getSignature().getName(),
                        joinPoint.getTarget().getClass().getSimpleName(),
                        executionTime,
                        requestMethod, requestURI,
                        throwable.getMessage());
            } else {
                logger.error("Not HTTP request in {} threw exception after {} ms. Exception: {}",
                        joinPoint.getTarget().getClass().getSimpleName(),
                        executionTime,
                        throwable.getMessage());
            }
            throw throwable;
        }
    }
}