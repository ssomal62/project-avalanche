package site.leesoyeon.avalanche.product.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.product.shared.api.ApiStatus;
import site.leesoyeon.avalanche.product.presentation.dto.ProductCreateRequest;
import site.leesoyeon.avalanche.product.presentation.dto.ProductDetailResponse;
import site.leesoyeon.avalanche.product.presentation.dto.ProductListResponse;
import site.leesoyeon.avalanche.product.presentation.dto.ProductUpdateRequest;
import site.leesoyeon.avalanche.product.domain.model.Product;
import site.leesoyeon.avalanche.product.shared.enums.ProductStatus;
import site.leesoyeon.avalanche.product.infrastructure.exception.ProductException;
import site.leesoyeon.avalanche.product.infrastructure.persistence.repository.ProductRepository;
import site.leesoyeon.avalanche.product.application.util.ProductMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public UUID createProduct(ProductCreateRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return savedProduct.getProductId();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(UUID id) {
        Product product = findById(id);
        return productMapper.toDetailResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductListResponse getProductList() {
        List<Product> products = productRepository.findAll();
        List<ProductDetailResponse> productDetailsList = products.stream()
                .map(productMapper::toDetailResponse)
                .collect(Collectors.toList());
        return new ProductListResponse(productDetailsList);
    }

    @Transactional
    public void updateProduct(ProductUpdateRequest request) {
        Product product = findById(request.productId());
        productMapper.updateEntityFromRequest(request, product);
    }

    @Transactional
    public void deactivateProduct(UUID id) {
        Product product = findById(id);
        product.updateStatus(ProductStatus.DISCONTINUED);
    }

    @Transactional
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

//     ============================================
//                 Protected Methods
//     ============================================

    protected Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductException(ApiStatus.NOT_FOUND_PRODUCT));
    }
}
