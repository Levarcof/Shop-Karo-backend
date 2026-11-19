package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String roleUser);
}
