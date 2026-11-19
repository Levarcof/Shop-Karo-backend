package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.dtos.OrderFilterRequest;
import ecommerce.app.Ecommerce.entity.Order;
import ecommerce.app.Ecommerce.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderSpecifications {

    private OrderSpecifications(){}

    public  static Specification<Order> withFilters(OrderFilterRequest filter  , LocalDateTime start , LocalDateTime end){
        return (root , query , cb)->{
            List<Predicate> predicates = new ArrayList<>();

            if(filter.getStatus() != null){
                predicates.add(
                        cb.equal(root.get("status") , filter.getStatus())
                );
            }

            if(filter.getUserId() != null){
                Join<Order, User> user =
                        root.join("user", JoinType.INNER);
                predicates.add(
                        cb.equal(
                                user.get("id"), filter.getUserId()
                        )
                );
            }

            if(filter.getSearch() != null &&  !filter.getSearch().isBlank()){
                String search = "%" + escapeLike(filter.getSearch().trim().toLowerCase(Locale.ROOT)) + "%";
                Join<Order , User> user = root.join("user" , JoinType.INNER);

                Predicate userName = cb.like(
                        cb.lower(user.<String>get("name")) ,
                        search ,
                        '\\');

                Predicate userEmail = cb.like(
                        cb.lower(user.<String>get("email")),
                        search,
                        '\\'
                );

                predicates.add(
                        cb.or(userEmail , userName)
                );
            }

            if(filter.getMinAmount() != null){
                predicates.add(
                        cb.greaterThanOrEqualTo(root.<BigDecimal>get("totalAmount") , filter.getMinAmount())
                );
            }

            if(filter.getMaxAmount() != null){
                predicates.add(
                        cb.lessThanOrEqualTo(root.<BigDecimal>get("totalAmount") , filter.getMaxAmount())
                );
            }

            if(start != null){
                predicates.add(
                        cb.greaterThanOrEqualTo(root.<LocalDateTime>get("createdAt") , start)
                );
            }

            if(end != null){
                predicates.add(
                        cb.lessThan(root.<LocalDateTime>get("createdAt") , end)
                );
            }

            return  cb.and(
                    predicates.toArray(new  Predicate[0])
            );
        };
    }

    private static String escapeLike(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

}
