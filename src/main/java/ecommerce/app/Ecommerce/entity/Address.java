package ecommerce.app.Ecommerce.entity;

import ecommerce.app.Ecommerce.enums.AddressType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
//@Table(name = "addresses",
//        indexes = @Index(name = "idx_address_user", columnList = "user_id"))
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String fullName;            // jis naam par delivery hogi

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false)
    private String line1;               // house no, building, street

    private String line2;               // area, landmark (optional)

    @Column(nullable = false, length = 60)
    private String city;

    @Column(nullable = false, length = 60)
    private String state;

    @Column(nullable = false, length = 10)
    private String pincode;

    @Column(nullable = false, length = 60)
    private String country = "India";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AddressType type = AddressType.HOME;

    @Column(nullable = false)
    private boolean isDefault = false;

    @Column(nullable = false)
    private boolean deleted = false;    // soft delete

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();











    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
