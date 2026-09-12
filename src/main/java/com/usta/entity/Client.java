package com.usta.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    // Relación Muchos a Muchos con tabla intermedia
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "client_products",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> purchasedProducts = new ArrayList<>();

    // Helpers solicitados
    public void addProduct(Product product) {
        purchasedProducts.add(product);
    }
    public void removeProduct(Product product) {
        purchasedProducts.remove(product);
    }
    public int getPurchasedCount() {
        return purchasedProducts.size();
    }
    public boolean hasProduct(Long productId) {
        return purchasedProducts.stream().anyMatch(p -> p.getId().equals(productId));
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Product> getPurchasedProducts() { return purchasedProducts; }
    public void setPurchasedProducts(List<Product> purchasedProducts) { this.purchasedProducts = purchasedProducts; }
}