package com.jusdrive.booking_service.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* com.jusdrive.booking_service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.toShortString();
        Object[] args = joinPoint.getArgs();

        logger.info("Entering: {} | Args: {}", methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            logger.info(" Exiting: {}  | Execution time: {} ms", methodName, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            logger.error("Exception in: {} | Message: {} | Execution time: {} ms", methodName, ex.getMessage(), duration, ex);
            throw ex;
        }
    }
}




// NOTES


// Advice           Type	Purpose
// @Before	        Logs before method execution
// @After	        Logs after method execution (regardless of outcome)
// @AfterReturning	Logs only when method returns successfully
// @AfterThrowing	Logs only when an exception is thrown
// @Around	             Measures execution time and wraps the method
