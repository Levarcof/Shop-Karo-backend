package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category , Long> {

    boolean existsByName(String name);
}
