package online.aleksdraka.ecommerceapi.repositories;

import online.aleksdraka.ecommerceapi.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
