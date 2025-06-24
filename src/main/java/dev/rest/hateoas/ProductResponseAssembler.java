package dev.rest.hateoas;

import dev.rest.controller.ProductController;
import dev.rest.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductResponseAssembler implements RepresentationModelAssembler<ProductResponse, EntityModel<ProductResponse>> {
    
    // 모든 API 응답에 공통적으로 포함되어야할 링크 설정 메서드
    @Override
    public EntityModel<ProductResponse> toModel(ProductResponse product) {
        return EntityModel.of(product,
                linkTo(methodOn(ProductController.class).getProductById(product.id())).withSelfRel(),
                Link.of("/swagger-ui/index.html", "profile")  // API 문서 링크
        );
    }

    public Map<String, Link> buildPaginationLinks(String category, int page, int size, Page<?> productPage) {
        String basePath = "/api/products";
        String categoryQuery = (category != null && !category.isBlank()) ? "&category=" + category : "";

        Map<String, Link> links = new LinkedHashMap<>();
        links.put("profile", Link.of("/swagger-ui/index.html"));
        links.put("self", Link.of(basePath + "?page=" + page + "&size=" + size + categoryQuery));

        if (productPage.hasNext()) {
            links.put("next", Link.of(basePath + "?page=" + (page + 1) + "&size=" + size + categoryQuery));
        }
        if (productPage.hasPrevious()) {
            links.put("prev", Link.of(basePath + "?page=" + (page - 1) + "&size=" + size + categoryQuery));
        }

        return links;
    }

    // 상품 등록 API에만 포함되어야할 링크 설정 메서드
    public EntityModel<ProductResponse> toModelForCreate(ProductResponse product) {
        EntityModel<ProductResponse> baseModel = toModel(product); // 공통 링크는 무조건 포함되도록 지정

        // 상품 등록 API에만 포함될 링크들 지정
        baseModel.add(linkTo(methodOn(ProductController.class).getProducts(null, 0, 10)).withRel("list-products").withType("GET"));
        baseModel.add(linkTo(methodOn(ProductController.class).updateProduct(product.id(), null)).withRel("update-product").withType("PUT"));
        baseModel.add(linkTo(methodOn(ProductController.class).deleteProduct(product.id())).withRel("delete-product").withType("DELETE"));

        return baseModel;
    }

    public EntityModel<ProductResponse> toModelForDetail(ProductResponse product) {
        EntityModel<ProductResponse> baseModel = toModel(product); // self, profile 링크 포함

            baseModel.add(linkTo(methodOn(ProductController.class).updateProduct(product.id(), null))
                    .withRel("update-product").withType("PUT"));
            baseModel.add(linkTo(methodOn(ProductController.class).deleteProduct(product.id()))
                    .withRel("delete-product").withType("DELETE"));

        return baseModel;
    }

    public EntityModel<ProductResponse> toModelForUpdate(ProductResponse product) {
        EntityModel<ProductResponse> baseModel = toModel(product);

        baseModel.add(linkTo(methodOn(ProductController.class).getProductById(product.id()))
                .withRel("self").withType("GET"));
        baseModel.add(linkTo(methodOn(ProductController.class).getProducts(null, 0, 10))
                .withRel("list-products").withType("GET"));

        return baseModel;
    }

    public EntityModel<ProductResponse> toModelForDelete(ProductResponse product) {
        EntityModel<ProductResponse> baseModel = toModel(product);

        baseModel.add(linkTo(methodOn(ProductController.class).getProducts(null, 0, 10))
                .withRel("list-products").withType("GET"));

        return baseModel;
    }

}
