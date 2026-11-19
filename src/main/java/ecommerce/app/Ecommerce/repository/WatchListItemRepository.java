package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.WatchListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WatchListItemRepository extends JpaRepository<WatchListItem , Long> {

    @Query("SELECT i FROM WatchListItem i WHERE i.product.id = :productId AND i.watchList.id = :watchListId")
    Optional<WatchListItem> findByProductIdAndWatchListId(Long productId, Long watchListId);
}
