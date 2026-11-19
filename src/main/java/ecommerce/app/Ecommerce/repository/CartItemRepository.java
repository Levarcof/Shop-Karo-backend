package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem , Long> {

    @Query("SELECT i FROM CartItem i WHERE i.product.id = :productId AND i.cart.id = :cartId")
    Optional<CartItem> findByProductIdAndCartId(Long productId, Long cartId);
}
