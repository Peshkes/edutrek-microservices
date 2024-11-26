package com.telran.paymentservice.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            // Получаем HttpServletRequest из контекста запроса
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String requestMethod = request.getMethod();
            String requestURI = request.getRequestURI();
            String requestId = request.getHeader("X-Request-Id"); // Извлекаем X-Request-Id

            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            // Логируем метод, класс, время исполнения и запрос с добавленным уникальным номером запроса
            logger.info("RequestId: {} - Method {} in {} executed in {} ms. Request: {} {}",
                    requestId,
                    joinPoint.getSignature().getName(),
                    joinPoint.getTarget().getClass().getSimpleName(),
                    executionTime,
                    requestMethod, requestURI);

            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - start;
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String requestMethod = request.getMethod();
            String requestURI = request.getRequestURI();
            String requestId = request.getHeader("X-Request-Id"); // Извлекаем X-Request-Id

            // Логируем исключение с добавлением уникального номера запроса
            logger.error("RequestId: {} - Method {} in {} threw exception after {} ms. Request: {} {}. Exception: {}",
                    requestId,
                    joinPoint.getSignature().getName(),
                    joinPoint.getTarget().getClass().getSimpleName(),
                    executionTime,
                    requestMethod, requestURI,
                    throwable.getMessage());
            throw throwable;
        }
    }

    @Before("@annotation(Loggable)")
    public void logBefore(JoinPoint joinPoint) {
        // Получаем HttpServletRequest из контекста запроса
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String requestId = request.getHeader("X-Request-Id"); // Извлекаем X-Request-Id

        // Логируем начало метода и параметры запроса с добавлением уникального номера запроса
        logger.info("RequestId: {} - Method {} in {} started with arguments: {}. Request: {} {}",
                requestId,
                joinPoint.getSignature().getName(),
                joinPoint.getTarget().getClass().getSimpleName(),
                Arrays.toString(joinPoint.getArgs()),
                requestMethod, requestURI);
    }
}