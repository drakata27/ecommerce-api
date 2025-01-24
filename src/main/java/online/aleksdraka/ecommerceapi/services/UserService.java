package online.aleksdraka.ecommerceapi.services;

import online.aleksdraka.ecommerceapi.annotations.RequiresRole;
import online.aleksdraka.ecommerceapi.annotations.VerifyCart;
import online.aleksdraka.ecommerceapi.dtos.ProductDto;
import online.aleksdraka.ecommerceapi.models.Cart;
import online.aleksdraka.ecommerceapi.models.CartItem;
import online.aleksdraka.ecommerceapi.models.Product;
import online.aleksdraka.ecommerceapi.models.User;
import online.aleksdraka.ecommerceapi.repositories.CartRepository;
import online.aleksdraka.ecommerceapi.repositories.ProductRepository;
import online.aleksdraka.ecommerceapi.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Service
public class UserService {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CartRepository cartRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public ResponseEntity<String> registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
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

    @VerifyCart
    public ResponseEntity<Cart> getCartByUsername(String username, Long id) {
        Cart cart = cartRepository.findCartByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
            return ResponseEntity.ok(cart);
    }

    @Transactional
    @VerifyCart
    public ResponseEntity<?> addProductToCart(String username, Long id, List<ProductDto> productDtos) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            user.setCart(cart);
        }


        for (ProductDto productDto : productDtos) {
            Product product = productRepository.findById(productDto.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Check if product already exists in cart
            CartItem existingItem = cart.getItems().stream()
                    .filter(item -> item.getProduct().getId().equals(product.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                // Update quantity if product is already in the cart
                existingItem.setQuantity(existingItem.getQuantity() + productDto.getQuantity());
            } else {
                // Create a new cart item
                CartItem newItem = new CartItem();
                newItem.setProduct(product);
                newItem.setCart(cart);
                newItem.setQuantity(productDto.getQuantity());
                cart.getItems().add(newItem);
            }
        }

        cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }
}
