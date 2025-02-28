package online.aleksdraka.ecommerceapi.repositories;

import online.aleksdraka.ecommerceapi.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    User findUserById(Long id);
}
