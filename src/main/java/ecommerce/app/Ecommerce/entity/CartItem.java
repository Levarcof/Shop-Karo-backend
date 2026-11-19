package ecommerce.app.Ecommerce.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY , optional = false )
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "product_id" , nullable = false)
    private Product product;

    @Column(nullable = false , updatable = false)
    private LocalDateTime addedAt = LocalDateTime.now();










    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}