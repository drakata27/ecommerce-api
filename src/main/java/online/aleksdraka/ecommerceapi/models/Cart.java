package online.aleksdraka.ecommerceapi.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // From Cart to Product
    // This annotation ensures the products list is included in the JSON
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Product> products;

    // Cart owns the relationship
    // Create user_id in cart table
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // For Bidirectional consistency
    // TODO use bidirectional consistency
    public void addProduct(Product product) {
        products.add(product);
        product.setCart(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setCart(null);
    }

}
