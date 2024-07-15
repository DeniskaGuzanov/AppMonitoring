package com.example.etc.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Клас AOP для отслеживания запускаемых методов на время разработки и отладки
 */
//@Aspect
//@Component
public class UserActionTrackingAspect {
    @Around("execution(public * ru.slenergo.AppMonitoring..*.*(..))")
    public Object trackUserAction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String arguments = Arrays.toString(args);
        System.out.println("*Вызов метода: "
                + joinPoint.getSignature().getDeclaringTypeName() + "."
                + joinPoint.getSignature().getName() + ", аргументы: "
                + arguments);
        Object result = joinPoint.proceed();
        System.out.println("*Результат(" + joinPoint.getSignature().getName() + "): " + ((result != null) ? result.toString() : "null"));
        return result;
    }
}