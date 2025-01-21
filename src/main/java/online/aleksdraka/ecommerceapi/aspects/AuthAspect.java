package online.aleksdraka.ecommerceapi.aspects;

import online.aleksdraka.ecommerceapi.annotations.RequiresRole;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class AuthAspect {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Around("@annotation(requiresRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequiresRole requiresRole) throws Throwable {
        String requiredRole = requiresRole.value();

        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        if (userRole == null || !userRole.equals(requiredRole)) {
            logger.warning("Access denied for role: " + userRole);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        logger.info("Access granted for role: " + userRole);
        return joinPoint.proceed();
    }
}