package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address  , Long>{
    List<Address> findByUserIdAndDeletedFalse(Long id);

    Optional<Address> findByIdAndUser_IdAndDeletedFalse(Long id, Long userId);

    @Modifying
    @Query("""
    UPDATE Address a
    SET a.isDefault = false
    WHERE a.user.id = :userId
      AND a.isDefault = true
    """)
    int clearDefaultAddresses(@Param("userId") Long userId);
}
