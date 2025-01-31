package online.aleksdraka.ecommerceapi.aspects;

import online.aleksdraka.ecommerceapi.annotations.RequiresRole;
import online.aleksdraka.ecommerceapi.exceptions.EntityNotFoundException;
import online.aleksdraka.ecommerceapi.models.User;
import online.aleksdraka.ecommerceapi.repositories.UserRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

@Aspect
@Component
public class AuthAspect {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final UserRepository userRepository;

    public AuthAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    @Around("@annotation(online.aleksdraka.ecommerceapi.annotations.VerifyCart)")
    public Object checkCart(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String username = (String) args[0];
        Long id = (Long) args[1];

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!Objects.equals(id, user.getId())) {
            throw new EntityNotFoundException("Cart not found");
        }

        return joinPoint.proceed();
    }
}