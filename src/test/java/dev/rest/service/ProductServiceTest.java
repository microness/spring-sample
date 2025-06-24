package dev.rest.service;

import dev.rest.dto.ProductRequest;
import dev.rest.dto.ProductResponse;
import dev.rest.exception.ProductNotFoundException;
import dev.rest.model.Product;
import dev.rest.model.User;
import dev.rest.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("상품 생성시 정상적으로 저장되어야 한다")
    void given_product_request_when_create_product_then_product_saved() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encoded-password")
                .email("test@email.com")
                .build();
        ProductRequest request = new ProductRequest("테스트 상품", "상품 설명", 10000, 10, "전자제품");
        Product product = Product.from(request, user);

        given(productRepository.save(any(Product.class))).willReturn(product);

        // When
        ProductResponse response = productService.createProduct(request, user);

        // Then
        assertThat(response.name()).isEqualTo("테스트 상품");
        assertThat(response.price()).isEqualTo(10000);
        assertThat(response.stock()).isEqualTo(10);
    }

    @Nested
    @DisplayName("상품 수정 테스트")
    class UpdateProductTest {

        @Test
        @DisplayName("상품 ID가 존재하지 않으면 ProductNotFoundException이 발생해야 한다")
        void given_invalid_id_when_update_then_throws_product_not_found() {
            // Given
            Long invalidId = 99L;
            ProductRequest request = new ProductRequest("변경된 상품", "설명", 20000, 5, "카테고리");
            User user = User.builder().id(1L).build();

            given(productRepository.findById(invalidId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.updateProduct(invalidId, request, user))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("데이터가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("상품 등록자와 로그인한 사용자가 서로 다르면 AccessDeniedException이 발생한다")
        void given_wrong_user_when_update_then_throws_access_denied() {
            // Given
            Long productId = 1L;
            User owner = User.builder().id(1L).build();

            User another = User.builder().id(2L).build();
            ProductRequest request = new ProductRequest("변경된 상품명", "설명", 20000, 5, "카테고리");

            Product product = Product.builder()
                    .id(productId)
                    .name("원래 상품")
                    .description("설명")
                    .price(10000)
                    .stock(10)
                    .category("카테고리")
                    .user(owner)
                    .build();

            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // When & Then
            assertThatThrownBy(() -> productService.updateProduct(productId, request, another))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("해당 상품을 수정할 권한이 없습니다.");
        }

        @Test
        @DisplayName("정상적인 수정 요청 시 상품이 성공적으로 업데이트되어야 한다")
        void given_valid_request_when_update_then_success() {
            // Given
            Long productId = 1L;
            User owner = User.builder().id(1L).build();
            ProductRequest request = new ProductRequest("수정된 상품", "새 설명", 15000, 7, "가전");

            Product product = Product.builder()
                    .id(productId)
                    .name("원래 상품")
                    .description("설명")
                    .price(10000)
                    .stock(10)
                    .category("카테고리")
                    .user(owner)
                    .build();

            given(productRepository.findById(productId)).willReturn(Optional.of(product));
            given(productRepository.save(any(Product.class))).willReturn(product); // 수정된 객체 리턴

            // When
            ProductResponse response = productService.updateProduct(productId, request, owner);

            // Then
            assertThat(response.name()).isEqualTo("수정된 상품");
            assertThat(response.description()).isEqualTo("새 설명");
            assertThat(response.price()).isEqualTo(15000);
            assertThat(response.stock()).isEqualTo(7);
            assertThat(response.category()).isEqualTo("가전");
        }
    }

    // TODO: 상품 조회, 제거 테스트
}