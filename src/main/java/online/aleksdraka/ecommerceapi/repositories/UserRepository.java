package online.aleksdraka.ecommerceapi.repositories;

import online.aleksdraka.ecommerceapi.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
