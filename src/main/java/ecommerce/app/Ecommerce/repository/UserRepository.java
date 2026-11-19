package ecommerce.app.Ecommerce.repository;


import ecommerce.app.Ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String username);
    Optional<User> findByProviderAndProviderSubject(String provider, String providerSubject);
}
