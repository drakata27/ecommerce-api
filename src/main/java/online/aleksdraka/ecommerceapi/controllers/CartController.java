package online.aleksdraka.ecommerceapi.controllers;

import online.aleksdraka.ecommerceapi.models.Cart;
import online.aleksdraka.ecommerceapi.models.Product;
import online.aleksdraka.ecommerceapi.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class CartController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/carts/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCartById(userId);
    }

    @PostMapping("carts/{userId}/add")
    public ResponseEntity<Cart> addProducts(@PathVariable Long userId, @RequestBody List<Product> products) {
        try {
            cartService.addProducts(userId, products);
            return new ResponseEntity<>(cartService.getCartById(userId), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
