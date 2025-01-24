package online.aleksdraka.ecommerceapi.controllers;

import lombok.extern.slf4j.Slf4j;
import online.aleksdraka.ecommerceapi.config.JwtUtils;
import online.aleksdraka.ecommerceapi.models.User;
import online.aleksdraka.ecommerceapi.services.CustomUserDetailsService;
import online.aleksdraka.ecommerceapi.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return  userService.registerUser(user.getUsername(), user.getPassword());
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) throws AuthenticationException {
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                user.getPassword()
                        ));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        logger.info(user.getUsername() + "| " + role + " | " + user.getId());
        return jwtUtils.generateToken(userDetails.getUsername(), role);
    }
}
