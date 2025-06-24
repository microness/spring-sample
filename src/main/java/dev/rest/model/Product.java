package dev.rest.model;

import dev.rest.dto.ProductRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "products")
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int price;
    private int stock;
    private String category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Product(String name, String description, int price, int stock, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public Product(String name, String description, int price, int stock, String category, User user) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.user = user;
    }

    public static Product from(ProductRequest request, User user) {
        return new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category(),
                user
        );
    }

    public void update(ProductRequest request) {
        this.name = request.name();
        this.description = request.description();
        this.price = request.price();
        this.stock = request.stock();
        this.category = request.category();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
