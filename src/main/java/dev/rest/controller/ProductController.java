package dev.rest.controller;

import dev.rest.dto.ProductRequest;
import dev.rest.dto.ProductResponse;
import dev.rest.model.Product;
import dev.rest.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productService.getProducts(category, pageable);

        List<ProductResponse> productList = productPage.getContent().stream()
                .map(ProductResponse::from)
                .toList();

        return ResponseEntity.ok(productList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid  @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);

        URI location = URI.create("/api/products/" + response.id());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse updated = productService.updateProduct(id, request);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable Long id) {
        ProductResponse deleted = productService.deleteProduct(id);

        return ResponseEntity.ok(deleted);
    }
}
