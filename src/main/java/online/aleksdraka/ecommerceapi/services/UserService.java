package online.aleksdraka.ecommerceapi.services;

import online.aleksdraka.ecommerceapi.annotations.RequiresRole;
import online.aleksdraka.ecommerceapi.dtos.ProductDto;
import online.aleksdraka.ecommerceapi.models.Cart;
import online.aleksdraka.ecommerceapi.models.Product;
import online.aleksdraka.ecommerceapi.models.User;
import online.aleksdraka.ecommerceapi.repositories.CartRepository;
import online.aleksdraka.ecommerceapi.repositories.ProductRepository;
import online.aleksdraka.ecommerceapi.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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


    public ResponseEntity<Cart> getCartByUsername(String username, Long id) {
        Cart cart = cartRepository.findCartByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Objects.equals(id, user.getId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
            return ResponseEntity.ok(cart);
    }

    public ResponseEntity<?> addProductToCart(String username, Long id, List<ProductDto> productsDto) {
        Cart cart = cartRepository.findCartByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Objects.equals(id, user.getId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        for (ProductDto productDto : productsDto) {
            if (productRepository.existsById(productDto.getId())) {
                Product fetchedProduct = productRepository.findById(productDto.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                Product product = new Product();
                product.setId(productDto.getId());
                product.setName(fetchedProduct.getName());
                product.setPrice(fetchedProduct.getPrice());
                product.setQuantity(productDto.getQuantity());

                cart.addProduct(product);
            } else {
                return new ResponseEntity<>( "Product not found", HttpStatus.NOT_FOUND);
            }
        }

        return ResponseEntity.ok(cartRepository.save(cart));
    }

}
