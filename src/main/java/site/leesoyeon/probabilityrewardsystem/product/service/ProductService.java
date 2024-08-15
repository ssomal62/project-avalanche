package site.leesoyeon.probabilityrewardsystem.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.product.dto.ProductCreateRequest;
import site.leesoyeon.probabilityrewardsystem.product.dto.ProductDetailResponse;
import site.leesoyeon.probabilityrewardsystem.product.dto.ProductListResponse;
import site.leesoyeon.probabilityrewardsystem.product.dto.ProductUpdateRequest;
import site.leesoyeon.probabilityrewardsystem.product.entity.Product;
import site.leesoyeon.probabilityrewardsystem.product.enums.ProductStatus;
import site.leesoyeon.probabilityrewardsystem.product.exception.ProductException;
import site.leesoyeon.probabilityrewardsystem.product.repository.ProductRepository;
import site.leesoyeon.probabilityrewardsystem.product.util.ProductMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.NOT_FOUND_PRODUCT;

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
        return productRepository.findById(id).orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT));
    }
}
