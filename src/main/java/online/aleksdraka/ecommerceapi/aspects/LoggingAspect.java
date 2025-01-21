package online.aleksdraka.ecommerceapi.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    @Around("execution(* online.aleksdraka.ecommerceapi.services.*.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Method: " + joinPoint.getSignature().getName() + ": will execute");
        Object returnedByMethod = joinPoint.proceed();
        logger.info("Method: " + joinPoint.getSignature().getName() + ": executed");
        return returnedByMethod;
    }
}
