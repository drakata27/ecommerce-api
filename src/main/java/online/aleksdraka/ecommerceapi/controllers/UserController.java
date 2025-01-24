package online.aleksdraka.ecommerceapi.controllers;

import online.aleksdraka.ecommerceapi.dtos.ProductDto;
import online.aleksdraka.ecommerceapi.dtos.StripeResponse;
import online.aleksdraka.ecommerceapi.models.Cart;
import online.aleksdraka.ecommerceapi.models.User;
import online.aleksdraka.ecommerceapi.services.StripeService;
import online.aleksdraka.ecommerceapi.services.UserService;
import org.springframework.http.HttpStatus;
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
    private final StripeService stripeService;

    public UserController(UserService userService, StripeService stripeService) {
        this.userService = userService;
        this.stripeService = stripeService;
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
        return userService.addProductToCart(username,id, productsDto);
    }

    @PostMapping("/users/{id}/cart/checkout")
    public ResponseEntity<?> checkoutCart(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody List<ProductDto> productsDto
    ) {
        String username = authentication.getName();
        StripeResponse stripeResponse = stripeService.checkoutProducts(username,id, productsDto);
        if (stripeResponse == null) {
            return new ResponseEntity<>("Cart not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

}
