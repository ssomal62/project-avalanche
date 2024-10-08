package site.leesoyeon.avalanche.product.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.product.application.service.ProductService;
import site.leesoyeon.avalanche.product.presentation.dto.ProductCreateRequest;
import site.leesoyeon.avalanche.product.presentation.dto.ProductDetailResponse;
import site.leesoyeon.avalanche.product.presentation.dto.ProductListResponse;
import site.leesoyeon.avalanche.product.presentation.dto.ProductUpdateRequest;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping
//    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<UUID> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        UUID response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProduct(
            @PathVariable(value = "productId") UUID productId) {
        ProductDetailResponse response = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<ProductListResponse> getProduct() {
        ProductListResponse response = productService.getProductList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping
//    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateProduct(
            @Valid @RequestBody ProductUpdateRequest request) {
        productService.updateProduct(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{productId}")
//    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deactivateProduct(
            @PathVariable(value = "productId") UUID productId) {
        productService.deactivateProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
