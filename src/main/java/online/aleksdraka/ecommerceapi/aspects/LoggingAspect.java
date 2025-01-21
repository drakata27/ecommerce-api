package online.aleksdraka.ecommerceapi.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    public void log(final JoinPoint joinPoint) {}
}
