package dev.rest.controller;

import dev.rest.dto.ProductPageResponse;
import dev.rest.dto.ProductRequest;
import dev.rest.dto.ProductResponse;
import dev.rest.exception.ErrorResponse;
import dev.rest.hateoas.ProductResponseAssembler;
import dev.rest.model.Product;
import dev.rest.model.User;
import dev.rest.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "상품 REST API", description = "상품 관리 API 설명입니다.")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductResponseAssembler assembler;

    @GetMapping
    public ResponseEntity<ProductPageResponse> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productService.getProducts(category, pageable);

        List<ProductResponse> productList = productPage.getContent().stream()
                .map(ProductResponse::from)
                .toList();

        Map<String, Link> links = assembler.buildPaginationLinks(category, page, size, productPage);

        return ResponseEntity.ok(new ProductPageResponse(productList, links));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        EntityModel<ProductResponse> model = assembler.toModelForDetail(response, response.userId());
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "상품 등록", description = "인증된 사용자는 자신의 상품을 등록할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "상품 등록 성공",
                    content = @Content(
                            mediaType = "application/hal+json",
                            schema = @Schema(implementation = ProductPageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효성 검증 실패 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE) //Content-type을 HAL(Hypertext Application Language) 타입으로 설정
    public ResponseEntity<EntityModel<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request, @AuthenticationPrincipal User user) {
        ProductResponse response = productService.createProduct(request, user);

        // HATEOAS 설정
        EntityModel<ProductResponse> model = assembler.toModelForCreate(response);

        return ResponseEntity
                .created(linkTo(methodOn(ProductController.class).getProductById(response.id())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal User user
    ) {
        ProductResponse updated = productService.updateProduct(id, request, user);
        EntityModel<ProductResponse> model = assembler.toModelForUpdate(updated, user.getId());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<ProductResponse>> deleteProduct(@PathVariable Long id, @AuthenticationPrincipal User user) {
        ProductResponse deleted = productService.deleteProduct(id, user);
        EntityModel<ProductResponse> model = assembler.toModelForDelete(deleted);
        return ResponseEntity.ok(model);
    }
}
