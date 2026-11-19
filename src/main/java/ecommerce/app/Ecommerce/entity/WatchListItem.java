package ecommerce.app.Ecommerce.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class WatchListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY , optional = false )
    @JoinColumn(name = "wathlist_id")
    private WatchList watchList;

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

    public WatchList getWatchList() {
        return watchList;
    }

    public void setWatchList(WatchList watchList) {
        this.watchList = watchList;
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
