package online.aleksdraka.ecommerceapi.services;

import online.aleksdraka.ecommerceapi.annotations.RequiresRole;
import online.aleksdraka.ecommerceapi.models.Product;
import online.aleksdraka.ecommerceapi.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequiresRole("ROLE_ADMIN")
    public ResponseEntity<Product> addProduct(Product product) {
        productRepository.save(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @RequiresRole("ROLE_ADMIN")
    public ResponseEntity<Product> updateProduct(Long id, Product newProduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        product.setName(newProduct.getName());
        product.setDescription(newProduct.getDescription());
        product.setPrice(newProduct.getPrice());
        productRepository.save(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @RequiresRole("ROLE_ADMIN")
    public ResponseEntity<?> deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
