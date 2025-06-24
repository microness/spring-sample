package dev.rest.service;

import dev.rest.dto.ProductRequest;
import dev.rest.dto.ProductResponse;
import dev.rest.exception.ProductNotFoundException;
import dev.rest.model.Product;
import dev.rest.model.User;
import dev.rest.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

    public ProductResponse createProduct(ProductRequest request, User user) {

        Product newProduct = Product.from(request, user);

        Product createdProduct = productRepository.save(newProduct);

        return ProductResponse.from(createdProduct);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request, User user) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // 등록자와 현재 로그인한 사용자가 일치하는지 확인
        if (!product.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("해당 상품을 수정할 권한이 없습니다.");
        }

        product.update(request);
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse deleteProduct(Long id, User user) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // 등록자와 현재 로그인한 사용자가 일치하는지 확인
        if (!product.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("해당 상품을 제거할 권한이 없습니다.");
        }

        // 삭제 전에 응답용 DTO로 변환
        ProductResponse deletedProduct = ProductResponse.from(product);

        productRepository.delete(product);

        return deletedProduct;
    }
}
