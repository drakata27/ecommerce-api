package online.aleksdraka.ecommerceapi.repositories;

import online.aleksdraka.ecommerceapi.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
