package online.aleksdraka.ecommerceapi.services;

import jakarta.persistence.EntityNotFoundException;
import online.aleksdraka.ecommerceapi.models.Cart;
import online.aleksdraka.ecommerceapi.models.Product;
import online.aleksdraka.ecommerceapi.models.User;
import online.aleksdraka.ecommerceapi.repositories.CartRepository;
import online.aleksdraka.ecommerceapi.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class CartService {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    public Cart getCartById(Long id) {
        return cartRepository.findCartByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }

    public void addProducts(Long userId, List<Product> products) {

        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        log.info("User Id: " + user.getId() + ", Cart user Id: " + cart.getUser().getId());

        if (Objects.equals(cart.getUser().getId(), userId)) {
            for (Product product : products) {
                cart.addProduct(product);
            }

            cartRepository.save(cart);
            log.info("Products successfully added to the cart for User Id: " + userId);
        } else {
            log.info("Unauthorized access attempt: User Id " + userId +
                    " does not match Cart owner Id " + cart.getUser().getId());

            throw new SecurityException("Unauthorized access to cart");
        }
    }

}

