package online.aleksdraka.ecommerceapi.services;

import online.aleksdraka.ecommerceapi.annotations.RequiresRole;
import online.aleksdraka.ecommerceapi.models.User;
import online.aleksdraka.ecommerceapi.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    @RequiresRole("ROLE_ADMIN")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        userRepository.findAll();
        return ResponseEntity.ok(userRepository.findAll());
    }

    @RequiresRole("ROLE_ADMIN")
    public ResponseEntity<User> getUserById(Long id) {
        return ResponseEntity.ok(userRepository.findUserById(id));
    }
}
