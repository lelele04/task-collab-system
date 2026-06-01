package com.teamtask.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;
import java.time.LocalDateTime;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(* com.teamtask.controller..*(..))")
    public void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void beforeMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        logger.info("[操作日志] 类: {}, 方法: {}, 参数: {}, 时间: {}",
                className, methodName, args, LocalDateTime.now());
    }

    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void afterReturningMethod(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        logger.info("[操作日志] 类: {}, 方法: {}, 返回值: {}, 时间: {}",
                className, methodName, result, LocalDateTime.now());
    }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "exception")
    public void afterThrowingMethod(JoinPoint joinPoint, Throwable exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        logger.error("[错误日志] 类: {}, 方法: {}, 异常: {}, 时间: {}",
                className, methodName, exception.getMessage(), LocalDateTime.now());
    }
}