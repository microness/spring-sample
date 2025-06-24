package dev.rest.service;

import dev.rest.dto.ProductRequest;
import dev.rest.dto.ProductResponse;
import dev.rest.exception.ProductNotFoundException;
import dev.rest.model.Product;
import dev.rest.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<Product> getProducts(String category, Pageable pageable) {
        return (category != null && !category.isBlank())
                ? productRepository.findByCategory(category, pageable)
                : productRepository.findAll(pageable);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return ProductResponse.from(product);
    }

    public ProductResponse createProduct(ProductRequest request) {

        Product newProduct = Product.from(request);

        Product createdProduct = productRepository.save(newProduct);

        return ProductResponse.from(createdProduct);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.update(request);
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // 삭제 전 응답용 DTO로 변환
        ProductResponse deletedProduct = ProductResponse.from(product);

        productRepository.delete(product);

        return deletedProduct;
    }
}
