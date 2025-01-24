package online.aleksdraka.ecommerceapi.controllers;

import online.aleksdraka.ecommerceapi.dtos.ProductDto;
import online.aleksdraka.ecommerceapi.models.Cart;
import online.aleksdraka.ecommerceapi.models.Product;
import online.aleksdraka.ecommerceapi.models.User;
import online.aleksdraka.ecommerceapi.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class UserController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users/{id}/cart")
    public ResponseEntity<Cart> getCartByUserId(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();
        logger.info("Getting cart for user " + username);
        return userService.getCartByUsername(username, id);
    }

    @PostMapping("/users/{id}/cart")
    public ResponseEntity<?> addProductsToCart(Authentication authentication, @PathVariable Long id, @RequestBody List<ProductDto> productsDto) {
        String username = authentication.getName();
        return userService.addProductToCart(username, id, productsDto);
    }
}
